package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityPiston.class)
public abstract class MixinTileEntityPiston extends TileEntity {
	@Unique
	private float cuteful$progress;

	@Inject(method = "getProgress", at = @At("HEAD"), cancellable = true)
	public void getSmoothProgress(final float partialTicks, final CallbackInfoReturnable<Float> cir) {
		if (Configuration.smootherPistons) {
			cir.setReturnValue(Math.min(1F, (2F * this.cuteful$progress + partialTicks) / 3F));
		}
	}

	@Inject(method = "update", at = @At("HEAD"))
	private void progressIncrementer(final CallbackInfo ci) {
		this.cuteful$progress += 0.5F;
	}

	@Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;removeTileEntity(Lnet/minecraft/util/math/BlockPos;)V"), cancellable = true)
	private void mc54546(final CallbackInfo ci) {
		if (Configuration.smootherPistons && this.world.isRemote) {
			ci.cancel();
		}
	}
}
