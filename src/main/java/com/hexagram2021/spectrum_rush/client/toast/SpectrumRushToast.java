package com.hexagram2021.spectrum_rush.client.toast;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public record SpectrumRushToast(DyeColor dyeColor, ItemStack wool) implements Toast {
	private static final int DISPLAY_TIME = 5000;
	private static final Component DESCRIPTION = Component.translatable("spectrum_rush.toast.description");

	public SpectrumRushToast(DyeColor dyeColor, Item wool) {
		this(dyeColor, new ItemStack(wool));
	}

	@Override
	public Visibility render(GuiGraphics transform, ToastComponent toastComponent, long ticks) {
		transform.blit(TEXTURE, 0, 0, 0, 0, this.width(), this.height());
		List<FormattedCharSequence> titleList = toastComponent.getMinecraft().font.split(this.wool.getItem().getDescription(), 125);
		List<FormattedCharSequence> contentList = toastComponent.getMinecraft().font.split(DESCRIPTION, 125);
		if (contentList.size() == 1) {
			transform.drawString(toastComponent.getMinecraft().font, titleList.get(0), 30, 7, this.dyeColor.getTextColor(), false);
			transform.drawString(toastComponent.getMinecraft().font, contentList.get(0), 30, 18, -1, false);
			transform.renderFakeItem(this.wool, 8, 8);
		} else {
			contentList = toastComponent.getMinecraft().font.split(DESCRIPTION, 144);
			if (ticks < 1500L) {
				int k = Mth.floor(Mth.clamp((float) (1500L - ticks) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 0x04000000;
				transform.drawString(toastComponent.getMinecraft().font, titleList.get(0), 30, 11, this.dyeColor.getTextColor() | k, false);
				transform.renderFakeItem(this.wool, 8, 8);
			} else {
				int i1 = Mth.floor(Mth.clamp((float) (ticks - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 0x04000000;
				int l = this.height() / 2 - contentList.size() * 9 / 2;

				for (FormattedCharSequence formattedcharsequence : contentList) {
					transform.drawString(toastComponent.getMinecraft().font, formattedcharsequence, 8, l, 0xFFFFFF | i1, false);
					l += 9;
				}
			}
		}

		return (double) ticks >= DISPLAY_TIME * toastComponent.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
	}
}
