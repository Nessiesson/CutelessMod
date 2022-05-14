package net.dugged.cutelessmod.mixins;

import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer {
	@ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;setOnlineMode(Z)V"))
	private boolean localServerIsAlwaysOfflineMode(final boolean value) {
		return true;
	}
}
