package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.renderer.block.model.WeightedBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

public abstract class CMixinNoRandomTextures {
	@Mixin(WeightedBakedModel.class)
	public abstract static class MixinWeightedBakedModel {
		@ModifyVariable(method = "getQuads", at = @At("HEAD"), argsOnly = true)
		private long cutelessmod$noRandomTextures(final long value) {
			return Configuration.showRandomTextures ? value : 0L;
		}
	}
}
