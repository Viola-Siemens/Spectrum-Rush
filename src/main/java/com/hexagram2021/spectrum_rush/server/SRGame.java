package com.hexagram2021.spectrum_rush.server;

import com.hexagram2021.spectrum_rush.common.config.SRCommonConfig;
import com.hexagram2021.spectrum_rush.common.utils.LevelUtils;
import com.hexagram2021.spectrum_rush.register.SRSpawnTypes;
import com.mojang.brigadier.Command;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;

import java.util.Collection;
import java.util.UUID;

public final class SRGame {
	public static int roundRemainingTicks = 0;
	public static int remainingRounds = 0;
	public static DyeColor currentColor = DyeColor.BLACK;

	private static final UUID SPEED_MODIFIER_SHEEP_UUID = UUID.fromString("53e053ac-cdcb-45bd-b857-0b1cdd281432");

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
		remainingRounds = SRCommonConfig.TOTAL_ROUNDS.get();
		roundRemainingTicks = SRCommonConfig.EACH_ROUND_TICKS.get();
		currentColor = DyeColor.byName(SRCommonConfig.SHEEP_COLORS.get().get(level.random.nextInt(SRCommonConfig.SHEEP_COLORS.get().size())), DyeColor.BLACK);
		return Command.SINGLE_SUCCESS;
	}

	/**
	 * 游戏每一刻的逻辑
	 */
	public static void tickGame(ServerLevel level) {
		if(roundRemainingTicks > 0) {
			--roundRemainingTicks;
		} else {
			if(remainingRounds > 0) {
				--remainingRounds;
				roundRemainingTicks = SRCommonConfig.EACH_ROUND_TICKS.get();
				currentColor = DyeColor.byName(SRCommonConfig.SHEEP_COLORS.get().get(level.random.nextInt(SRCommonConfig.SHEEP_COLORS.get().size())), DyeColor.BLACK);
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
					Component.literal(SRCommonConfig.SUBTITLE_MESSAGE.get().formatted(SRCommonConfig.EACH_ROUND_TICKS.get() / SharedConstants.TICKS_PER_SECOND, currentColor.getSerializedName()))
			));
		});
		return Command.SINGLE_SUCCESS;
	}

	private SRGame() {
	}
}
