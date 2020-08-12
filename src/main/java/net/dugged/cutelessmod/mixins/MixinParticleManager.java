package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public abstract class MixinParticleManager {
	@Inject(method = "addBlockDestroyEffects", at = @At("HEAD"), cancellable = true)
	private void onAddBlockDestroyEffects(BlockPos pos, IBlockState state, CallbackInfo ci) {
		if (!Configuration.showBlockBreakingParticles) {
			ci.cancel();
		}
	}
}
