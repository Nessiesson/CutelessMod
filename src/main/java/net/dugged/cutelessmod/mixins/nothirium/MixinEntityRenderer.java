package net.dugged.cutelessmod.mixins.nothirium;

import meldexun.renderlib.renderer.entity.EntityRenderer;
import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {
	@Inject(method = "shouldRenderOutlines(Lnet/minecraft/entity/Entity;)Z", remap = false, at = @At("HEAD"), cancellable = true)
	private void highlightAllEntitites(final Entity entity, final CallbackInfoReturnable<Boolean> cir) {
		if (CutelessMod.highlightEntities) {
			cir.setReturnValue(entity instanceof EntityLivingBase || entity instanceof EntityMinecart);
		}
	}
}
