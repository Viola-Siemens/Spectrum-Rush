package com.hexagram2021.spectrum_rush.common.config;

import net.minecraft.world.item.DyeColor;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class SRCommonConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.BooleanValue STARTED;
	public static final ForgeConfigSpec.IntValue EACH_ROUND_TICKS;
	public static final ForgeConfigSpec.IntValue TOTAL_ROUNDS;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> SHEEP_COLORS;
	public static final ForgeConfigSpec.IntValue COUNT_SHEEP_EACH_COLOR;
	public static final ForgeConfigSpec.IntValue MAX_SPAWN_RADIUS;
	public static final ForgeConfigSpec.ConfigValue<String> SUBTITLE_MESSAGE;

	static {
		BUILDER.push("spectrum_rush-common-config");
			BUILDER.push("game-mechanism");
				STARTED = BUILDER.comment("If the game is started.").define("START", false);
				SHEEP_COLORS = BUILDER.comment("The colors of sheep. Please avoid using black.").define("SHEEP_COLORS", List.of(
						DyeColor.WHITE.getName(),
						DyeColor.ORANGE.getName(),
						DyeColor.MAGENTA.getName(),
						DyeColor.LIGHT_BLUE.getName(),
						DyeColor.YELLOW.getName(),
						DyeColor.LIME.getName(),
						DyeColor.PINK.getName(),
						DyeColor.GRAY.getName(),
						DyeColor.LIGHT_GRAY.getName(),
						DyeColor.CYAN.getName()
				), o -> o instanceof String str && DyeColor.byName(str, null) != null);
				EACH_ROUND_TICKS = BUILDER.comment("Total time of each round (in ticks).").defineInRange("EACH_ROUND_TICKS", 1200, 0, 60 * 60 * 20);
				TOTAL_ROUNDS = BUILDER.comment("How many rounds will we play.").defineInRange("TOTAL_ROUNDS", 10, 0, 100);
				COUNT_SHEEP_EACH_COLOR = BUILDER.comment("The count of sheep each color.").defineInRange("COUNT_SHEEP_EACH_COLOR", 10, 1, 100);
				MAX_SPAWN_RADIUS = BUILDER.comment("The maximum spawn radius in blocks.").defineInRange("MAX_SPAWN_RADIUS", 160, 1, 1024);
				SUBTITLE_MESSAGE = BUILDER.comment("The subtitle message when a round is started.").define("SUBTITLE_MESSAGE", "You have %d seconds to shear %s wools as many as possible.");
			BUILDER.pop();
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

	public static ForgeConfigSpec getSpec() {
		return SPEC;
	}
}
