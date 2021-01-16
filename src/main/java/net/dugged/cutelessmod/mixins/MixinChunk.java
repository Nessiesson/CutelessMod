package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public abstract class MixinChunk {
	@Inject(method = "relightBlock", at = @At("HEAD"), cancellable = true)
	private void noLightLagEverPlz(final int x, final int y, final int z, final CallbackInfo ci) {
		if (!Configuration.lightUpdates) {
			ci.cancel();
		}
	}
}
