package com.hexagram2021.spectrum_rush.mixin.client;

import com.hexagram2021.spectrum_rush.client.toast.SpectrumRushToast;
import com.hexagram2021.spectrum_rush.common.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(Gui.class)
public class GuiMixin {
	@Shadow @Final
	protected Minecraft minecraft;

	@Inject(method = "setTitle", at = @At(value = "TAIL"))
	private void spectrum_rush$setTitle(Component component, CallbackInfo ci) {
		if(component.getContents() instanceof TranslatableContents translatableContents) {
			Pattern pattern = Pattern.compile("^block\\.minecraft\\.([a-z0-9_]+)_wool$");
			Matcher matcher = pattern.matcher(translatableContents.getKey());
			if(matcher.matches()) {
				String colorName = matcher.group(1);
				DyeColor color = DyeColor.byName(colorName, null);
				if(color != null) {
					this.minecraft.getToasts().addToast(new SpectrumRushToast(color, Constants.SHEEP_WOOLS.get(color)));
				}
			}
		}
	}
}
