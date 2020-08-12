package net.dugged.cutelessmod.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/client/renderer/EntityRenderer$1")
public abstract class MixinEntityRenderer_Predicate {
	@Inject(method = "apply(Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
	private void ignoreDeadEntities(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getHealth() <= 0F) {
			cir.setReturnValue(false);
		}
	}
}
