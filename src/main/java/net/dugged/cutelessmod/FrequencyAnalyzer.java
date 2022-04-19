package net.dugged.cutelessmod;

import net.dugged.cutelessmod.mixins.IBlockRedstoneDiode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FrequencyAnalyzer {
	public static BlockPos position = null;
	private static long lastTick = 0;
	// most common of int[5] to prevent random spikes
	private static int[] frequencyArray = new int[5];
	private static boolean oldState = false;

	public static void render(float partialTicks) {
		if (position != null) {
			CutelessModUtils.drawCube(partialTicks, position, 250, 49, 0);
			CutelessModUtils.drawString(partialTicks, Integer.toString(CutelessModUtils.getMostCommon(frequencyArray.clone())), position.getX() + 0.5f, position.getY() + 0.5f, position.getZ() + 0.5f, 0);
		}
	}

	public static void update() {
		if (position != null) {
			boolean updatedState = getState(position);
			if (updatedState != oldState && updatedState) {
				int frequency = (int) (CutelessMod.tickCounter - lastTick);
				if (CutelessMod.mspt > 0) {
					frequency = (int) (frequency / 20f * (1000 / CutelessMod.mspt));
				}
				for (int i = frequencyArray.length - 1; i > 0; --i) {
					frequencyArray[i] = frequencyArray[i - 1];
				}
				frequencyArray[0] = frequency;
				lastTick = CutelessMod.tickCounter;
			}
			oldState = updatedState;
		}
	}

	private static boolean getState(BlockPos pos) {
		World world = Minecraft.getMinecraft().world;
		if (world != null) {
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			if (block instanceof BlockRedstoneWire) {
				return state.getValue(BlockRedstoneWire.POWER) > 0;
			}
			if (block instanceof BlockRedstoneComparator) {
				return state.getValue(BlockRedstoneComparator.POWERED);
			}
			if (block instanceof BlockRedstoneRepeater) {
				return ((IBlockRedstoneDiode) state.getBlock()).getIsRepeaterPowered();
			}
		}
		for (int i = frequencyArray.length - 1; i > 0; --i) {
			frequencyArray[i] = 0;
		}
		return false;
	}

	public static void reset() {
		frequencyArray = new int[5];
		lastTick = 0;
		oldState = false;
	}
}
