package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.PistonHelper;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.minecraft.block.BlockPistonBase.EXTENDED;

@Mixin(BlockPistonBase.class)
public abstract class MixinBlockPistonBase extends BlockDirectional {
	@Shadow
	@Final
	private boolean isSticky;

	protected MixinBlockPistonBase(Material materialIn) {
		super(materialIn);
	}

	@Inject(method = "checkForMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addBlockEvent(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;II)V", ordinal = 1, shift = At.Shift.BEFORE))
	private void mc88959(final World world, final BlockPos pos, final IBlockState state, final CallbackInfo ci) {
		if (Configuration.instantDoubleRetraction) {
			world.setBlockState(pos, state.withProperty(EXTENDED, false), 2);
		}
	}

	@Unique
	private static boolean shouldKeepPistonHead = false;

	@Inject(method = "eventReceived", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockPistonBase;doMove(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Z)Z", ordinal = 1))
	private void stickyBlockRetraction(final IBlockState state, final World world, final BlockPos pos, final int retracting, final int meta, final CallbackInfoReturnable<Boolean> cir) {
		if (Configuration.smootherPistons && world.isRemote) {
			MixinBlockPistonBase.shouldKeepPistonHead = true;
		}
	}

	@Inject(method = "eventReceived", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockPistonBase;doMove(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Z)Z", ordinal = 1, shift = Shift.BY, by = 3), locals = LocalCapture.CAPTURE_FAILHARD)
	private void stickyEmptyRetraction(final IBlockState state, final World world, final BlockPos pos, final int retracting, final int meta, final CallbackInfoReturnable<Boolean> cir, final EnumFacing facing) {
		if (Configuration.smootherPistons && world.isRemote) {
			if (!MixinBlockPistonBase.shouldKeepPistonHead) {
				world.setBlockToAir(pos.offset(facing));
			}

			MixinBlockPistonBase.shouldKeepPistonHead = false;
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!Configuration.showPistonOrder) return false;
		boolean flag = playerIn.getHeldItem(EnumHand.MAIN_HAND).isEmpty() && playerIn.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.AIR;
		if (worldIn.isRemote && flag) {
			boolean extending = !(Boolean) state.getValue(BlockPistonBase.EXTENDED);
			if ((!pos.equals(PistonHelper.pistonPos) || !PistonHelper.activated) && (extending || isSticky)) {
				PistonHelper.setPistonMovement(worldIn, state, pos, extending);
			} else {
				PistonHelper.activated = false;
			}
			if (worldIn.isRemote) {
				return isSticky || !(Boolean) state.getValue(BlockPistonBase.EXTENDED);
			}
		}
		return flag;
	}
}
