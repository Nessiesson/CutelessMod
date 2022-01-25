package net.dugged.cutelessmod.clientcommands.worldedit;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BrushRemoveColumn extends BrushBase {
	@Override
	public String getName() {
		return "removeColumn";
	}

	@Override
	public ArrayList<BlockPos> run(World world, BlockPos pos) {
		ArrayList<BlockPos> positions = new ArrayList<>();
		while (world.getBlockState(pos).getMaterial() != Material.AIR) {
			positions.add(pos);
			pos = pos.up();
		}
		return positions;
	}
}
