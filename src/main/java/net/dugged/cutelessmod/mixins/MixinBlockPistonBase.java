package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.block.BlockPistonBase.EXTENDED;

@Mixin(BlockPistonBase.class)
public abstract class MixinBlockPistonBase {
	@Inject(method = "checkForMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addBlockEvent(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;II)V", ordinal = 1, shift = At.Shift.BEFORE))
	private void mc88959(final World world, final BlockPos pos, final IBlockState state, final CallbackInfo ci) {
		if (Configuration.instantDoubleRetraction) {
			world.setBlockState(pos, state.withProperty(EXTENDED, false), 2);
		}
	}
}
