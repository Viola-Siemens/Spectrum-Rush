package com.hexagram2021.spectrum_rush.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class SRCommands {
	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal("spectrumrush").then(
				Commands.literal("start")
						.requires(stack -> stack.hasPermission(2))
						.executes(context -> SRGame.startGame(context.getSource().getLevel()))
		).then(
				Commands.literal("stop")
						.requires(stack -> stack.hasPermission(2))
						.executes(context -> SRGame.stopGame(context.getSource().getLevel()))
		);
	}
}
