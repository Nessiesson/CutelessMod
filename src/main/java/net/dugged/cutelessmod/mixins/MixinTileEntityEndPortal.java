package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityEndPortal.class)
public abstract class MixinTileEntityEndPortal extends TileEntity {
	@Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
	private void cutelessmod$renderMoreEndPortalFaces(final EnumFacing face, final CallbackInfoReturnable<Boolean> cir) {
		if (Configuration.showMoreEndPortalFaces) {
			final var state = this.world.getBlockState(this.getPos().offset(face));
			cir.setReturnValue(!state.isOpaqueCube() && state.getBlock() != Blocks.END_PORTAL);
		}
	}
}
