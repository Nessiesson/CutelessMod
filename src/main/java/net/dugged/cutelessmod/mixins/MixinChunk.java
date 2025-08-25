package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public abstract class MixinChunk {
	@Shadow
	@Final
	private World world;

	@Inject(method = "relightBlock", at = @At("HEAD"), cancellable = true)
	private void noLightLagEverPlz(final int x, final int y, final int z, final CallbackInfo ci) {
		if (!Configuration.lightUpdates && this.world.isRemote) {
			ci.cancel();
		}
	}
}
