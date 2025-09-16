package com.hexagram2021.spectrum_rush.mixin;

import com.hexagram2021.spectrum_rush.register.SRSpawnTypes;
import net.minecraft.world.entity.MobSpawnType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(MobSpawnType.class)
public class MobSpawnTypeMixin {
	@SuppressWarnings("unused")
	MobSpawnTypeMixin(String name, int ord) {
		throw new UnsupportedOperationException("Replaced by Mixin");
	}

	@Shadow @Final @Mutable
	private static MobSpawnType[] $VALUES;

	/**
	 * 添加 Spectrum Rush Spawn Type
	 * @param ci	Callback Info
	 */
	@Inject(method = "<clinit>()V", at = @At(value = "FIELD", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/MobSpawnType;$VALUES:[Lnet/minecraft/world/entity/MobSpawnType;"))
	private static void spectrum_rush$injectEnum(CallbackInfo ci) {
		int ordinal = $VALUES.length;
		$VALUES = Arrays.copyOf($VALUES, ordinal + 1);

		SRSpawnTypes.SPECTRUM_RUSH = $VALUES[ordinal] = (MobSpawnType)(Object)new MobSpawnTypeMixin("SPECTRUM_RUSH$SPECTRUM_RUSH", ordinal);
	}
}
