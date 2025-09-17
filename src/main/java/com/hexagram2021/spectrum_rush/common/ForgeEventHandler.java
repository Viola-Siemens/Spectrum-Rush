package com.hexagram2021.spectrum_rush.common;

import com.hexagram2021.spectrum_rush.SpectrumRush;
import com.hexagram2021.spectrum_rush.common.config.SRCommonConfig;
import com.hexagram2021.spectrum_rush.register.SRSpawnTypes;
import com.hexagram2021.spectrum_rush.server.SRCommands;
import com.hexagram2021.spectrum_rush.server.SRGame;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SpectrumRush.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {
	/**
	 * 阻止小游戏生成的实体被删除
	 * @param event	事件
	 */
	@SubscribeEvent
	public static void onEntityDespawn(MobSpawnEvent.AllowDespawn event) {
		if(SRCommonConfig.STARTED.get() && event.getEntity().getSpawnType() == SRSpawnTypes.SPECTRUM_RUSH) {
			event.setResult(MobSpawnEvent.Result.DENY);
		}
	}

	/**
	 * 阻止小游戏过程中生成其他实体
	 * @param event	事件
	 */
	@SubscribeEvent
	public static void onEntitySpawn(MobSpawnEvent.FinalizeSpawn event) {
		if(SRCommonConfig.STARTED.get() && event.getSpawnType() != SRSpawnTypes.SPECTRUM_RUSH) {
			event.setSpawnCancelled(true);
			event.setCanceled(true);
		}
	}

	/**
	 * 注册命令
	 * @param event	事件
	 */
	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		dispatcher.register(SRCommands.register());
	}

	/**
	 * 监听游戏世界每 tick 的更新，对应更新小游戏逻辑
	 * @param event	事件
	 */
	@SubscribeEvent
	public static void onLevelTick(TickEvent.LevelTickEvent event) {
		if(event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END && SRCommonConfig.STARTED.get() && event.level instanceof ServerLevel serverLevel && serverLevel.dimension() == Level.OVERWORLD) {
			SRGame.tickGame(serverLevel);
		}
	}
}
