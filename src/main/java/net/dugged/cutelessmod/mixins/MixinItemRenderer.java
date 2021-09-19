package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {


	@Redirect(method = "updateEquippedItem()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;getCooledAttackStrength(F)F"))
	private float disableItemSwapAnimation(EntityPlayerSP playerSP, float adjustTicks) {
		if (!Configuration.showHandChangeAnimation) {
			return 1.0F;
		} else {
			return playerSP.getCooledAttackStrength(adjustTicks);
		}

	}
}
