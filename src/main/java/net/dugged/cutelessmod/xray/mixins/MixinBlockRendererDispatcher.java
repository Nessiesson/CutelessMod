package net.dugged.cutelessmod.xray.mixins;

import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRendererDispatcher.class)
public abstract class MixinBlockRendererDispatcher {
	@Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true)
	private void cutelessmod$xray$stopBlockRender(final IBlockState state, final BlockPos pos, final IBlockAccess world, final BufferBuilder buffer, final CallbackInfoReturnable<Boolean> cir) {
		if (CutelessMod.xray.isHiddenByXray(state)) {
			cir.setReturnValue(false);
		}
	}
}
