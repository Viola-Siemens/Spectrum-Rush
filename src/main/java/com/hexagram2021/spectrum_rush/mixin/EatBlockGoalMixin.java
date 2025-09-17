package com.hexagram2021.spectrum_rush.mixin;

import com.hexagram2021.spectrum_rush.register.SRSpawnTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EatBlockGoal.class)
public class EatBlockGoalMixin {
	@Shadow @Final
	private Mob mob;

	/**
	 * 修改羊吃草的间隔时间
	 * @param constant	50 或 1000，分别对应幼年和成年羊的吃草间隔时间
	 * @return 修改后的间隔时间
	 */
	@ModifyConstant(method = "canUse", constant = {@Constant(intValue = 50), @Constant(intValue = 1000)})
	private int spectrum_rush$modifyEatBlockGoalTicks(int constant) {
		return this.mob.getSpawnType() == SRSpawnTypes.SPECTRUM_RUSH ? constant / 5 : constant;
	}
}
