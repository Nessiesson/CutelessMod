package net.dugged.cutelessmod.mixins.nothirium;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
	@Inject(method = "checkGLError", at = @At("HEAD"), cancellable = true)
	private void onCheckGLError(final String message, final CallbackInfo ci) {
		ci.cancel();
	}
}
