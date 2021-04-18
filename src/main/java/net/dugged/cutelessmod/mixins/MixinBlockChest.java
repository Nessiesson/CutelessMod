package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.DoubleChestSide;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockChest.Type;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.function.Supplier;

@Mixin(BlockChest.class)
public abstract class MixinBlockChest extends BlockContainer {
	@Shadow
	@Final
	protected static AxisAlignedBB NORTH_CHEST_AABB;
	@Shadow
	@Final
	protected static AxisAlignedBB SOUTH_CHEST_AABB;
	@Shadow
	@Final
	protected static AxisAlignedBB WEST_CHEST_AABB;
	@Shadow
	@Final
	protected static AxisAlignedBB EAST_CHEST_AABB;

	public MixinBlockChest(final Material material) {
		super(material);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void appendToDefaultState(final Type chest, final CallbackInfo ci) {
		this.setDefaultState(this.getDefaultState().withProperty(DoubleChestSide.AABB, DoubleChestSide.NONE));
	}

	@ModifyArg(method = "createBlockState", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/BlockStateContainer;<init>(Lnet/minecraft/block/Block;[Lnet/minecraft/block/properties/IProperty;)V"))
	private IProperty<?>[] onCreateBlockState(IProperty<?>[] properties) {
		properties = Arrays.copyOf(properties, properties.length + 1);
		properties[properties.length - 1] = DoubleChestSide.AABB;
		return properties;
	}

	@Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
	private void onGetRenderType(final IBlockState state, final CallbackInfoReturnable<EnumBlockRenderType> cir) {
		if (Configuration.chestWithoutTESR) {
			cir.setReturnValue(EnumBlockRenderType.MODEL);
		}
	}

	@Override
	public IBlockState getActualState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		final AxisAlignedBB bb = state.getBoundingBox(world, pos);
		final EnumFacing face = ((Supplier<EnumFacing>) () -> {
			if (bb.equals(NORTH_CHEST_AABB)) return EnumFacing.NORTH;
			if (bb.equals(SOUTH_CHEST_AABB)) return EnumFacing.SOUTH;
			if (bb.equals(WEST_CHEST_AABB)) return EnumFacing.WEST;
			if (bb.equals(EAST_CHEST_AABB)) return EnumFacing.EAST;
			return EnumFacing.UP;
		}).get();

		final EnumFacing chestDir = state.getValue(BlockChest.FACING);
		final DoubleChestSide dir = DoubleChestSide.getSide(chestDir, face);
		return state.withProperty(DoubleChestSide.AABB, dir);
	}
}
