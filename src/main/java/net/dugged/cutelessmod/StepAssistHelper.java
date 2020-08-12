package net.dugged.cutelessmod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;

public class StepAssistHelper {
	public void update(EntityPlayer player) {
		player.stepHeight = getStepAmount(player);
	}

	private float getStepAmount(EntityPlayer player) {
		if (Configuration.stepAssist) {
			return player.isSneaking() ? 0.9F : 1.5F;
		}

		if (!Configuration.jumpBoostStepAssist || !player.isPotionActive(MobEffects.JUMP_BOOST)) {
			return 0.6F;
		}

		return player.isSneaking() ? 0.9F : 1F + 0.5F * (1 + player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier());
	}
}
