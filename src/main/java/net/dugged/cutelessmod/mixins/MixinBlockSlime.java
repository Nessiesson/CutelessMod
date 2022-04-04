package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.PistonHelper;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockSlime.class)
public abstract class MixinBlockSlime extends BlockBreakable {
	protected MixinBlockSlime(Material materialIn, boolean ignoreSimilarityIn) {
		super(materialIn, ignoreSimilarityIn);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!Configuration.showPistonOrder) return false;
		boolean flag = playerIn.getHeldItem(EnumHand.MAIN_HAND).isEmpty() && playerIn.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.AIR;
		if (worldIn.isRemote && flag) {
			pos = pos.offset(facing);
			state = new BlockPistonBase(false).getDefaultState().withProperty(BlockPistonBase.FACING, facing.getOpposite()).withProperty(BlockPistonBase.EXTENDED, Boolean.FALSE);
			if (!PistonHelper.activated || !pos.equals(PistonHelper.pistonPos)) {
				PistonHelper.setPistonMovement(worldIn, state, pos, true);
			} else {
				PistonHelper.activated = false;
			}
		}
		return flag;
	}
}
