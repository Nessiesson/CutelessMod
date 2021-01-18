package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerArmorBase.class)
public abstract class MixinLayerArmorBase {
	@Inject(method = "doRenderLayer", at = @At("HEAD"), cancellable = true)
	private void hideArmor(final EntityLivingBase entity, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale, final CallbackInfo ci) {
		if (!Configuration.showArmor && entity instanceof EntityPlayer) {
			ci.cancel();
		}
	}
}