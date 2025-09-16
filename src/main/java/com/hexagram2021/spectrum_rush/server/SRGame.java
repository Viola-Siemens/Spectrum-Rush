package com.hexagram2021.spectrum_rush.server;

import com.hexagram2021.spectrum_rush.common.config.SRCommonConfig;
import com.hexagram2021.spectrum_rush.common.utils.LevelUtils;
import com.hexagram2021.spectrum_rush.register.SRSpawnTypes;
import com.mojang.brigadier.Command;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.*;

public final class SRGame {
	public static int roundRemainingTicks = 0;
	public static int remainingRounds = 0;
	public static DyeColor currentColor = DyeColor.BLACK;

	private static final String SCORE_NAME = "spectrum_rush:score";
	private static final UUID SPEED_MODIFIER_SHEEP_UUID = UUID.fromString("53e053ac-cdcb-45bd-b857-0b1cdd281432");

	private static final EnumMap<DyeColor, Item> SHEEP_WOOLS = Util.make(new EnumMap<>(DyeColor.class), map -> {
		map.put(DyeColor.BLACK, Items.BLACK_WOOL);
		map.put(DyeColor.BLUE, Items.BLUE_WOOL);
		map.put(DyeColor.BROWN, Items.BROWN_WOOL);
		map.put(DyeColor.CYAN, Items.CYAN_WOOL);
		map.put(DyeColor.GRAY, Items.GRAY_WOOL);
		map.put(DyeColor.GREEN, Items.GREEN_WOOL);
		map.put(DyeColor.LIGHT_BLUE, Items.LIGHT_BLUE_WOOL);
		map.put(DyeColor.LIGHT_GRAY, Items.LIGHT_GRAY_WOOL);
		map.put(DyeColor.LIME, Items.LIME_WOOL);
		map.put(DyeColor.MAGENTA, Items.MAGENTA_WOOL);
		map.put(DyeColor.ORANGE, Items.ORANGE_WOOL);
		map.put(DyeColor.PINK, Items.PINK_WOOL);
		map.put(DyeColor.PURPLE, Items.PURPLE_WOOL);
		map.put(DyeColor.RED, Items.RED_WOOL);
		map.put(DyeColor.WHITE, Items.WHITE_WOOL);
		map.put(DyeColor.YELLOW, Items.YELLOW_WOOL);
	});

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
			entity.kill();
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
						attributeInstance.addTransientModifier(new AttributeModifier(SPEED_MODIFIER_SHEEP_UUID, "Spectrum Rush Speed Modifier", -0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL));
					}
				}
			}
		}

		Scoreboard scoreboard = level.getScoreboard();
		Objective objective = scoreboard.addObjective(SCORE_NAME, ObjectiveCriteria.DUMMY, Component.literal("Spectrum Rush Score"), ObjectiveCriteria.RenderType.INTEGER);
		level.players().forEach(player -> scoreboard.getOrCreatePlayerScore(player.getDisplayName().getString(), objective).setScore(0));

		remainingRounds = SRCommonConfig.TOTAL_ROUNDS.get();
		roundRemainingTicks = SRCommonConfig.EACH_ROUND_TICKS.get();
		currentColor = DyeColor.byName(SRCommonConfig.SHEEP_COLORS.get().get(level.random.nextInt(SRCommonConfig.SHEEP_COLORS.get().size())), DyeColor.BLACK);
		return sendMessage(level);
	}

	/**
	 * 游戏每一刻的逻辑
	 */
	public static void tickGame(ServerLevel level) {
		if(roundRemainingTicks % 10 == 0) {
			Scoreboard scoreboard = level.getScoreboard();
			Item currentWool = SHEEP_WOOLS.get(currentColor);
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
					level.playSeededSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_HARP, SoundSource.RECORDS, 3.0F, 1.0F, level.random.nextLong());
				}
			});
		}
		if(roundRemainingTicks > 0) {
			--roundRemainingTicks;
		} else {
			if(remainingRounds > 0) {
				--remainingRounds;
				roundRemainingTicks = SRCommonConfig.EACH_ROUND_TICKS.get();
				currentColor = DyeColor.byName(SRCommonConfig.SHEEP_COLORS.get().get(level.random.nextInt(SRCommonConfig.SHEEP_COLORS.get().size())), DyeColor.BLACK);
				sendMessage(level);
			} else {
				stopGame(level);
			}
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
			entity.kill();
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
			} else if(playerScoreList.size() > 1 && playerScoreList.get(0).score() > 0) {
				limit = 1;
			}
			if(limit > 0) {
				for (int i = 0; i < limit; ++i) {
					PlayerScore playerScore = playerScoreList.get(i);
					stringBuilder.append("Top ").append(i + 1).append(": ").append(playerScore.name()).append(" (").append(playerScore.score()).append(")\n");
				}
				level.players().forEach(player -> {
					player.sendSystemMessage(Component.literal(stringBuilder.toString()));
					player.sendSystemMessage(Component.literal("Congratulations to " + playerScoreList.get(0).name() + "!").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD));
				});
			}
			scoreboard.removeObjective(objective);
		}
		return Command.SINGLE_SUCCESS;
	}

	/**
	 * 向所有玩家发送需要采集的羊毛颜色的信息
	 * @return 命令执行成功返回 1
	 */
	public static int sendMessage(ServerLevel level) {
		level.players().forEach(player -> {
			player.connection.send(new ClientboundSetTitleTextPacket(
					Component.translatable("block.minecraft.%s_wool".formatted(currentColor.getSerializedName())).withStyle(Style.EMPTY.withColor(currentColor.getTextColor()))
			));
			player.connection.send(new ClientboundSetSubtitleTextPacket(
					Component.literal(SRCommonConfig.SUBTITLE_MESSAGE.get().formatted((roundRemainingTicks + 1) / SharedConstants.TICKS_PER_SECOND, currentColor.getSerializedName()))
			));
		});
		return Command.SINGLE_SUCCESS;
	}

	private SRGame() {
	}

	private record PlayerScore(String name, int score) implements Comparable<PlayerScore> {
		@Override
		public int compareTo(PlayerScore o) {
			return Integer.compare(this.score, o.score);
		}
	}
}
