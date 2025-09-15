package com.hexagram2021.spectrum_rush;

import com.hexagram2021.spectrum_rush.common.config.SRCommonConfig;
import com.hexagram2021.spectrum_rush.common.utils.LevelUtils;
import com.hexagram2021.spectrum_rush.register.SRSpawnTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Collection;

@Mod(SpectrumRush.MODID)
public class SpectrumRush {
	public static final String MODID = "spectrum_rush";

	public SpectrumRush() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SRCommonConfig.getSpec());
	}

	/**
	 * 启动游戏
	 * @param level	世界
	 */
	public static void startGame(ServerLevel level) {
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
				}
			}
		}
	}

	public static void stopGame(ServerLevel level) {
		assert level.dimension() == Level.OVERWORLD;
		SRCommonConfig.STARTED.set(false);
		SRCommonConfig.STARTED.save();
		Collection<? extends Sheep> entities = level.getEntities(EntityTypeTest.forClass(Sheep.class), entity -> entity.getSpawnType() == SRSpawnTypes.SPECTRUM_RUSH);
		for(Sheep entity : entities) {
			entity.kill();
		}
	}
}
