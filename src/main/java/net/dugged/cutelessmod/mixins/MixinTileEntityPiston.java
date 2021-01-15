package net.dugged.cutelessmod.mixins;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityPiston.class)
public abstract class MixinTileEntityPiston extends TileEntity {
	@Shadow
	private float progress;

	@Inject(method = "getProgress", at = @At("HEAD"), cancellable = true)
	public void onGetProgress(final float partialTicks, final CallbackInfoReturnable<Float> cir) {
		if (this.tileEntityInvalid && Math.abs(this.progress - 1F) < 1E-5F) {
			cir.setReturnValue(1F);
		}

		cir.setReturnValue(Math.min(1F, (2F * this.progress + partialTicks) / 3F));
	}
}
