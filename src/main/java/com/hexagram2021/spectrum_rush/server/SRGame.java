package com.hexagram2021.spectrum_rush.server;

import com.hexagram2021.spectrum_rush.common.config.SRCommonConfig;
import com.hexagram2021.spectrum_rush.common.utils.Constants;
import com.hexagram2021.spectrum_rush.common.utils.LevelUtils;
import com.hexagram2021.spectrum_rush.register.SRSpawnTypes;
import com.mojang.brigadier.Command;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Spectrum Rush 游戏逻辑
 */
public final class SRGame {
	public static int roundRemainingTicks = 0;
	public static int remainingRounds = 0;
	public static DyeColor currentColor = DyeColor.BLACK;

	private static final String SCORE_NAME = "spectrum_rush:score";
	private static final UUID SPEED_MODIFIER_SHEEP_UUID = UUID.fromString("53e053ac-cdcb-45bd-b857-0b1cdd281432");

	/**
	 * 倒计时进度条
	 */
	private static final ServerBossEvent bossEvent = new ServerBossEvent(
			Component.literal(getColorName(currentColor)).withStyle(Style.EMPTY.withColor(currentColor.getTextColor())),
			BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS
	);

	/**
	 * 启动游戏
	 * @param level	世界
	 * @return 命令执行成功返回 1
	 */
	public static int startGame(ServerLevel level) {
		assert level.dimension() == Level.OVERWORLD;
		SRCommonConfig.STARTED.set(true);
		SRCommonConfig.STARTED.save();
		Collection<? extends Sheep> entities = level.getEntities(EntityTypeTest.forClass(Sheep.class), entity -> true);
		for(Sheep entity : entities) {
			entity.discard();
		}
		BlockPos spawnPos = level.getSharedSpawnPos();
		int maxSpawnRadius = SRCommonConfig.MAX_SPAWN_RADIUS.get();
		for(String color : SRCommonConfig.SHEEP_COLORS.get()) {
			DyeColor dyeColor = DyeColor.byName(color, DyeColor.BLACK);
			for(int i = 0; i < SRCommonConfig.COUNT_SHEEP_EACH_COLOR.get(); ++i) {
				BlockPos blockPos = spawnPos.offset(
						level.random.nextInt(maxSpawnRadius * 2) - maxSpawnRadius,
						0,
						level.random.nextInt(maxSpawnRadius * 2) - maxSpawnRadius
				);
				blockPos = blockPos.atY(LevelUtils.getTopBlock(level, blockPos));
				Sheep sheep = EntityType.SHEEP.spawn(level, blockPos, SRSpawnTypes.SPECTRUM_RUSH);
				if (sheep != null) {
					sheep.setColor(dyeColor);
					AttributeInstance attributeInstance = sheep.getAttribute(Attributes.MOVEMENT_SPEED);
					if (attributeInstance != null) {
						attributeInstance.addTransientModifier(new AttributeModifier(
								SPEED_MODIFIER_SHEEP_UUID, "Spectrum Rush Speed Modifier",
								SRCommonConfig.SHEEP_SPEED_MODIFIER.get(), AttributeModifier.Operation.MULTIPLY_TOTAL
						));
					}
				}
			}
		}

		Scoreboard scoreboard = level.getScoreboard();
		Objective objective = scoreboard.addObjective(SCORE_NAME, ObjectiveCriteria.DUMMY, Component.literal("Spectrum Rush Score"), ObjectiveCriteria.RenderType.INTEGER);
		level.players().forEach(player -> {
			player.connection.send(new ClientboundSoundPacket(Holder.direct(SoundEvents.PLAYER_LEVELUP), SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0F, 1.0F, 0L));
			player.getInventory().clearOrCountMatchingItems(itemStack -> Constants.SHEEP_WOOLS.containsValue(itemStack.getItem()) || itemStack.is(Items.SHEARS),-1, player.inventoryMenu.getCraftSlots());
			player.addItem(new ItemStack(Items.SHEARS));
			scoreboard.getOrCreatePlayerScore(player.getDisplayName().getString(), objective).setScore(0);
			bossEvent.addPlayer(player);
		});
		scoreboard.setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, objective);

		remainingRounds = SRCommonConfig.TOTAL_ROUNDS.get() - 1;
		roundRemainingTicks = SRCommonConfig.EACH_ROUND_TICKS.get();
		currentColor = DyeColor.byName(SRCommonConfig.SHEEP_COLORS.get().get(level.random.nextInt(SRCommonConfig.SHEEP_COLORS.get().size())), DyeColor.BLACK);
		return sendMessage(level);
	}

	/**
	 * 游戏每一刻的逻辑
	 * @param level 世界
	 */
	public static void tickGame(ServerLevel level) {
		if(roundRemainingTicks % 10 == 0) {
			Scoreboard scoreboard = level.getScoreboard();
			Item currentWool = Constants.SHEEP_WOOLS.get(currentColor);
			level.players().forEach(player -> {
				Inventory inventory = player.getInventory();
				boolean flag = false;
				for(int i = 0; i < inventory.getContainerSize(); ++i) {
					ItemStack itemStack = inventory.getItem(i);
					if(itemStack.is(currentWool)) {
						Objective objective = scoreboard.getObjective(SCORE_NAME);
						if(objective != null) {
							Score score = scoreboard.getOrCreatePlayerScore(player.getDisplayName().getString(), objective);
							score.setScore(score.getScore() + itemStack.getCount());
							flag = true;
						}
						inventory.setItem(i, ItemStack.EMPTY);
					}
				}
				if(flag) {
					player.connection.send(new ClientboundSoundPacket(SoundEvents.NOTE_BLOCK_PLING, SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 2.0F, 1.0F, 0L));
				}
			});
		}
		if(roundRemainingTicks > 0) {
			--roundRemainingTicks;
			bossEvent.setProgress(roundRemainingTicks / (float) SRCommonConfig.EACH_ROUND_TICKS.get());
		} else if(remainingRounds > 0) {
			--remainingRounds;
			roundRemainingTicks = SRCommonConfig.EACH_ROUND_TICKS.get();
			currentColor = DyeColor.byName(SRCommonConfig.SHEEP_COLORS.get().get(level.random.nextInt(SRCommonConfig.SHEEP_COLORS.get().size())), DyeColor.BLACK);
			level.players().forEach(player -> player.getInventory().clearOrCountMatchingItems(itemStack -> Constants.SHEEP_WOOLS.containsValue(itemStack.getItem()),-1, player.inventoryMenu.getCraftSlots()));
			sendMessage(level);
		} else {
			stopGame(level);
		}
	}

	/**
	 * 停止游戏
	 * @param level 世界
	 * @return 命令执行成功返回 1
	 */
	public static int stopGame(ServerLevel level) {
		assert level.dimension() == Level.OVERWORLD;
		SRCommonConfig.STARTED.set(false);
		SRCommonConfig.STARTED.save();
		Collection<? extends Sheep> entities = level.getEntities(EntityTypeTest.forClass(Sheep.class), entity -> entity.getSpawnType() == SRSpawnTypes.SPECTRUM_RUSH);
		for(Sheep entity : entities) {
			entity.discard();
		}
		Scoreboard scoreboard = level.getScoreboard();
		Objective objective = scoreboard.getObjective(SCORE_NAME);
		if(objective != null) {
			List<PlayerScore> playerScoreList = level.players().stream()
					.map(player -> new PlayerScore(player.getDisplayName().getString(), scoreboard.getOrCreatePlayerScore(player.getDisplayName().getString(), objective).getScore()))
					.sorted(Comparator.reverseOrder()).toList();
			StringBuilder stringBuilder = new StringBuilder();
			int limit = 0;
			if(playerScoreList.size() > 5 && playerScoreList.get(4).score() > 0) {
				limit = 5;
			} else if(playerScoreList.size() > 3 && playerScoreList.get(2).score() > 0) {
				limit = 3;
			} else if(!playerScoreList.isEmpty() && playerScoreList.get(0).score() > 0) {
				limit = 1;
			}
			if(limit > 0) {
				for (int i = 0; i < limit; ++i) {
					PlayerScore playerScore = playerScoreList.get(i);
					stringBuilder.append("Top ").append(i + 1).append(": ").append(playerScore.name()).append(" (").append(playerScore.score()).append(")\n");
				}
				level.players().forEach(player -> {
					player.connection.send(new ClientboundSoundPacket(Holder.direct(SoundEvents.FIREWORK_ROCKET_LARGE_BLAST_FAR), SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0F, 1.0F, 0L));
					player.sendSystemMessage(Component.literal(stringBuilder.toString()));
					player.sendSystemMessage(Component.literal("Congratulations to " + playerScoreList.get(0).name() + "!").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD));
				});
			}
			scoreboard.setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, null);
			scoreboard.removeObjective(objective);
		}
		bossEvent.removeAllPlayers();
		return Command.SINGLE_SUCCESS;
	}

	/**
	 * 向所有玩家发送需要采集的羊毛颜色的信息
	 * @param level 世界
	 * @return 命令执行成功返回 1
	 */
	public static int sendMessage(ServerLevel level) {
		MutableComponent bossName = Component.translatable("block.minecraft.%s_wool".formatted(currentColor.getSerializedName())).withStyle(Style.EMPTY.withColor(currentColor.getTextColor()));
		bossEvent.setName(bossName);
		bossEvent.setProgress(roundRemainingTicks / (float) SRCommonConfig.EACH_ROUND_TICKS.get());
		level.players().forEach(player -> {
			player.connection.send(new ClientboundSetTitleTextPacket(bossName));
			player.sendSystemMessage(Component.literal(SRCommonConfig.SUBTITLE_MESSAGE.get().formatted((roundRemainingTicks + 1) / SharedConstants.TICKS_PER_SECOND, getColorName(currentColor))));
		});
		return Command.SINGLE_SUCCESS;
	}

	/**
	 * 获取颜色名称
	 * @param color	颜色
	 * @return 颜色名称
	 */
	@Contract(pure = true)
	private static String getColorName(DyeColor color) {
		char[] chars = color.getSerializedName().toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		for(int i = 1; i < chars.length; ++i) {
			if(chars[i] == '_') {
				chars[i] = ' ';
			} else if(chars[i - 1] == ' ') {
				chars[i] = Character.toUpperCase(chars[i]);
			} else {
				chars[i] = Character.toLowerCase(chars[i]);
			}
		}
		return new String(chars);
	}

	private SRGame() {
	}

	/**
	 * 玩家得分可排序的数据结构
	 * @param name	玩家名称
	 * @param score	得分
	 */
	private record PlayerScore(String name, int score) implements Comparable<PlayerScore> {
		@Override
		public int compareTo(PlayerScore o) {
			return Integer.compare(this.score, o.score);
		}
	}
}
