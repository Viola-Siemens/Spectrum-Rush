package com.hexagram2021.spectrum_rush;

import com.hexagram2021.spectrum_rush.common.config.SRCommonConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(SpectrumRush.MODID)
public class SpectrumRush {
	public static final String MODID = "spectrum_rush";

	public SpectrumRush() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SRCommonConfig.getSpec());
	}
}
