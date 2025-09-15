package com.hexagram2021.spectrum_rush.common;

import com.hexagram2021.spectrum_rush.SpectrumRush;
import com.hexagram2021.spectrum_rush.common.config.SRCommonConfig;
import com.hexagram2021.spectrum_rush.register.SRSpawnTypes;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SpectrumRush.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {
	@SubscribeEvent
	public static void onEntityDespawn(MobSpawnEvent.AllowDespawn event) {
		if(SRCommonConfig.STARTED.get() && event.getEntity().getSpawnType() == SRSpawnTypes.SPECTRUM_RUSH) {
			event.setResult(MobSpawnEvent.Result.DENY);
		}
	}

	@SubscribeEvent
	public static void onEntitySpawn(MobSpawnEvent.FinalizeSpawn event) {
		if(SRCommonConfig.STARTED.get() && event.getSpawnType() != SRSpawnTypes.SPECTRUM_RUSH) {
			event.setSpawnCancelled(true);
			event.setCanceled(true);
		}
	}
}
