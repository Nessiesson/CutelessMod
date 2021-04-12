package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockChest.class)
public abstract class MixinBlockChest {
	@Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
	private void onGetRenderType(final IBlockState state, final CallbackInfoReturnable<EnumBlockRenderType> cir) {
		if (Configuration.chestWithoutTESR) {
			cir.setReturnValue(EnumBlockRenderType.MODEL);
		}
	}
}
