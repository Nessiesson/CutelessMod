package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockCarpet.class, BlockSnow.class})
public abstract class CMixinThinBlockZFightingFix {
	@Inject(method = "isOpaqueCube", at = @At("HEAD"), cancellable = true)
	private void fixWeirdOpacity(final IBlockState state, final CallbackInfoReturnable<Boolean> cir) {
		if (Configuration.fixWeirdThinblockZFight) {
			cir.setReturnValue(true);
		}
	}
}
