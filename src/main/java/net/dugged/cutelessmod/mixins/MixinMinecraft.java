package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.util.IThreadListener;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IThreadListener, ISnooperInfo {
	@Shadow
	private int rightClickDelayTimer;

	@Inject(method = "dispatchKeypresses()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;setOptionValue(Lnet/minecraft/client/settings/GameSettings$Options;I)V"), cancellable = true)
	private void onNarratorKeypress(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "rightClickMouse", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;rightClickDelayTimer:I", opcode = Opcodes.PUTFIELD, ordinal = 0, shift = At.Shift.AFTER))
	private void onRightClick(CallbackInfo ci) {
		this.rightClickDelayTimer = Configuration.speedyPlace;
	}

	@ModifyArg(method = "runTickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F"), index = 2)
	private float allowFasterSpectatorFlight(float value) {
		return (float) Configuration.spectatorMaxSpeed;
	}
}
