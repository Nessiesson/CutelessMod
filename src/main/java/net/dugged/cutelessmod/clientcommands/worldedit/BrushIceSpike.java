package net.dugged.cutelessmod.clientcommands.worldedit;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class BrushIceSpike extends BrushBase {

	@Override
	public String getName() {
		return "iceSpike";
	}

	@Override
	public ArrayList<BlockPos> run(World world, BlockPos pos) {
		ArrayList<BlockPos> positions = new ArrayList<>();
		Random rand = new Random();
		while (world.isAirBlock(pos) && pos.getY() > 2) {
			pos = pos.down();
		}
		pos = pos.up(rand.nextInt(4));
		int i = rand.nextInt(4) + 7;
		int j = i / 4 + rand.nextInt(2);
		for (int k = 0; k < i; ++k) {
			float f = (1.0F - (float) k / (float) i) * (float) j;
			int l = MathHelper.ceil(f);
			for (int i1 = -l; i1 <= l; ++i1) {
				float f1 = (float) MathHelper.abs(i1) - 0.25F;
				for (int j1 = -l; j1 <= l; ++j1) {
					float f2 = (float) MathHelper.abs(j1) - 0.25F;
					if ((i1 == 0 && j1 == 0 || f1 * f1 + f2 * f2 <= f * f) && (i1 != -l && i1 != l && j1 != -l && j1 != l || rand.nextFloat() <= 0.75F)) {
						IBlockState blockState = world.getBlockState(pos.add(i1, k, j1));
						if (blockState.getMaterial() == Material.AIR) {
							positions.add(pos.add(i1, k, j1));
						}
						if (k != 0 && l > 1) {
							blockState = world.getBlockState(pos.add(i1, -k, j1));
							if (blockState.getMaterial() == Material.AIR) {
								positions.add(pos.add(i1, -k, j1));
							}
						}
					}
				}
			}
		}
		int k1 = j;
		if (k1 > 1) {
			k1 = 1;
		}
		int blocksDown = rand.nextInt(30);
		for (int l1 = -k1; l1 <= k1; ++l1) {
			for (int i2 = -k1; i2 <= k1; ++i2) {
				BlockPos blockpos = pos.add(l1, -1, i2);
				int j2 = 50;
				if (Math.abs(l1) == 1 && Math.abs(i2) == 1) {
					j2 = rand.nextInt(5);
				}
				int y = blocksDown;
				while (blockpos.getY() >= 10 && y-- >= 0) {
					IBlockState blockState = world.getBlockState(blockpos);
					if (blockState.getMaterial() == Material.AIR) {
						positions.add(blockpos);
					}
					blockpos = blockpos.down();
					--j2;
					if (j2 <= 0) {
						blockpos = blockpos.down(rand.nextInt(5) + 1);
						j2 = rand.nextInt(5);
					}
				}
			}
		}
		return positions;
	}
}
