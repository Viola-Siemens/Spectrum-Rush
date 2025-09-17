package com.hexagram2021.spectrum_rush.common.utils;

import net.minecraft.Util;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.EnumMap;

/**
 * Spectrum Rush 的常量类
 */
public final class Constants {
	/**
	 * 颜色对应的羊毛
	 */
	public static final EnumMap<DyeColor, Item> SHEEP_WOOLS = Util.make(new EnumMap<>(DyeColor.class), map -> {
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

	private Constants() {
	}
}
