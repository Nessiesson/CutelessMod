package net.dugged.cutelessmod.clientcommands.worldedit;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BrushPlaceTop extends BrushBase {
	@Override
	public String getName() {
		return "placeTop";
	}

	@Override
	public ArrayList<BlockPos> run(World world, BlockPos pos) {
		ArrayList<BlockPos> positions = new ArrayList<>();
		Block block = world.getBlockState(pos).getBlock();
		while (world.getBlockState(pos).getBlock() == block) {
			pos = pos.up();
		}
		positions.add(pos.down());
		return positions;
	}
}
