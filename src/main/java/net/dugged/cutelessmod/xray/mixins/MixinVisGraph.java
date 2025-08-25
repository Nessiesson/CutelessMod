package net.dugged.cutelessmod.xray.mixins;

import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VisGraph.class)
public abstract class MixinVisGraph {
	@Inject(method = "setOpaqueCube", at = @At("HEAD"), cancellable = true)
	private void cutelessmod$xray$fixWeirdHoles(final BlockPos pos, final CallbackInfo ci) {
		if (CutelessMod.xray.enabled) {
			ci.cancel();
		}
	}
}
