package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class MixinWorld {
	@Inject(method = "checkLightFor", at = @At("HEAD"), cancellable = true)
	private void onLight(CallbackInfoReturnable<Boolean> cir) {
		if (!Configuration.lightUpdates) {
			cir.setReturnValue(false);
		}
	}
}
