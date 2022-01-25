package net.dugged.cutelessmod.clientcommands.worldedit;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BrushPerimeterWall extends BrushBase {
	@Override
	public String getName() {
		return "perimeterWall";
	}

	@Override
	public ArrayList<BlockPos> run(World world, BlockPos pos) {
		ArrayList<BlockPos> positions = new ArrayList<>();
		int i = 0;
		BlockPos bottom = pos;
		BlockPos pos1 = pos;
		while (true) {
			if (checkSides(world, pos)) {
				i++;
			} else {
				i = 0;
			}
			if (i >= 10 || pos.getY() >= 255) {
				break;
			}
			if (i == 0) {
				pos1 = pos;
			}
			pos = pos.up();
		}
		while (bottom.getY() <= pos1.getY()) {
			positions.add(bottom);
			bottom = bottom.up();
		}
		return positions;
	}

	private boolean checkSides(World world, BlockPos pos) {
		ArrayList<BlockPos> posList = new ArrayList<>();
		posList.add(pos.north());
		posList.add(pos.east());
		posList.add(pos.south());
		posList.add(pos.west());
		posList.add(pos.north().east());
		posList.add(pos.north().west());
		posList.add(pos.south().east());
		posList.add(pos.south().west());
		for (BlockPos pos1 : posList) {
			if (world.getBlockState(pos1).getMaterial() != Material.AIR && world.getBlockState(pos1).getBlock() != getBlockState().getBlock()) {
				return false;
			}
		}
		return true;
	}
}
