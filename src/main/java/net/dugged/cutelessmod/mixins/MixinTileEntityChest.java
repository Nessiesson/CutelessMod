package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.CutelessMod;
import net.dugged.cutelessmod.DoubleChestUtils;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityChest.class)
public abstract class MixinTileEntityChest extends TileEntity {
	@Inject(method = "setNeighbor(Lnet/minecraft/tileentity/TileEntityChest;Lnet/minecraft/util/EnumFacing;)V", at = @At("RETURN"))
	private void onSetNeighbor(final TileEntityChest chest, final EnumFacing face, final CallbackInfo ci) {
		if (!this.world.isRemote) {
			return;
		}

		try {
			final IBlockState serverState = this.world.getBlockState(this.pos);
			final AxisAlignedBB bb = serverState.getBoundingBox(this.world, this.pos);
			final EnumFacing chestDir = serverState.getValue(BlockChest.FACING);
			DoubleChestUtils dir = DoubleChestUtils.NONE;
			if (bb.equals(IBlockChest.getNorthAABB())) {
				dir = DoubleChestUtils.getSide(chestDir, EnumFacing.NORTH);
			} else if (bb.equals(IBlockChest.getSouthAABB())) {
				dir = DoubleChestUtils.getSide(chestDir, EnumFacing.SOUTH);
			} else if (bb.equals(IBlockChest.getWestAABB())) {
				dir = DoubleChestUtils.getSide(chestDir, EnumFacing.WEST);
			} else if (bb.equals(IBlockChest.getEastAABB())) {
				dir = DoubleChestUtils.getSide(chestDir, EnumFacing.EAST);
			} else if (bb.equals(IBlockChest.getNoneAABB())) {
				dir = DoubleChestUtils.getSide(chestDir, EnumFacing.UP);
			}

			final IBlockState clientState = serverState.withProperty(DoubleChestUtils.AABB, dir);
			CutelessMod.LOGGER.info("{} {}", this.pos, clientState);
			this.world.setBlockState(this.pos, clientState);

		} catch (Throwable t) {
			CutelessMod.LOGGER.error(t.getStackTrace());
		}
	}
}
