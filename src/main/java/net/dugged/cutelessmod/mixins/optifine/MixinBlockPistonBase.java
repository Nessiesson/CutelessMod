package net.dugged.cutelessmod.mixins.optifine;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockPistonBase.class)
public abstract class MixinBlockPistonBase {
	@Inject(method = "eventReceived", at = @At("HEAD"))
	private void fixOptifineBlinkStart(final IBlockState state, final World world, final BlockPos pos, final int retracting, final int meta, final CallbackInfoReturnable<Boolean> cir) {
		// Semi works
		if (Configuration.smootherPistons) {
			((IPlayerControllerOF) Minecraft.getMinecraft().playerController).setActing(true);
		}
	}

	@Inject(method = "eventReceived", at = @At("RETURN"))
	private void fixOptifineBlinkStop(final IBlockState state, final World world, final BlockPos pos, final int retracting, final int meta, final CallbackInfoReturnable<Boolean> cir) {
		if (Configuration.smootherPistons) {
			((IPlayerControllerOF) Minecraft.getMinecraft().playerController).setActing(false);
		}
	}
}
