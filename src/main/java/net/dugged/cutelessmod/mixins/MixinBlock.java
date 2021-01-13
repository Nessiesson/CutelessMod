package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class MixinBlock {
	@Inject(method = "getOffset", at = @At("HEAD"), cancellable = true)
	private void onGetOffset(final IBlockState state, final IBlockAccess world, final BlockPos pos, final CallbackInfoReturnable<Vec3d> cir) {
		if (Configuration.showCenteredPlants) {
			cir.setReturnValue(Vec3d.ZERO);
		}
	}

	@ModifyArg(method = "registerBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;setTranslationKey(Ljava/lang/String;)Lnet/minecraft/block/Block;", ordinal = 0), slice = @Slice(from = @At(value = "NEW", target = "net/minecraft/block/BlockMushroom")))
	private static String brownMushroomTranslationKey(final String key) {
		return "brown_mushroom";
	}

	@ModifyArg(method = "registerBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;setTranslationKey(Ljava/lang/String;)Lnet/minecraft/block/Block;", ordinal = 1), slice = @Slice(from = @At(value = "NEW", target = "net/minecraft/block/BlockMushroom")))
	private static String redMushroomTranslationKey(final String key) {
		return "red_mushroom";
	}
}
