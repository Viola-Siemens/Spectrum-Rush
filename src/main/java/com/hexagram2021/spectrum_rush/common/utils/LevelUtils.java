package com.hexagram2021.spectrum_rush.common.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * 方块工具类
 */
public class LevelUtils {
	/**
	 * 获取指定位置的顶部方块高度
	 * @param level	世界
	 * @param pos	位置
	 * @return 顶部方块高度
	 */
	public static int getTopBlock(Level level, BlockPos pos) {
		int y = -1;

		if(level.dimension() == Level.OVERWORLD) {
			for (int j = level.getMaxBuildHeight() - 1; j > 40; j--) {
				BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos.getX(), 0, pos.getZ());
				if (!level.getBlockState(mutable.move(0, j, 0)).getBlock().defaultBlockState().isAir()) {
					y = j;
					break;
				}
			}
		}

		return y + 1;
	}
}
