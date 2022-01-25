package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
import net.dugged.cutelessmod.clientcommands.HandlerUndo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public abstract class BrushBase {

	private long useCooldown;

	private IBlockState blockState;

	private int radius;

	public abstract String getName();

	public abstract ArrayList<BlockPos> run(World world, BlockPos pos);

	public long getUseCooldown() {
		return useCooldown;
	}

	public void setUseCooldown(long useCooldown) {
		this.useCooldown = useCooldown;
	}

	public IBlockState getBlockState() {
		return blockState;
	}

	public void setBlockState(IBlockState blockState) {
		this.blockState = blockState;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public void execute(World world, BlockPos pos) {
		ArrayList<BlockPos> positions = run(world, pos);
		if (positions.size() > 0) {
			HandlerSetBlock setBlockHandler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world, null);
			HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, world, null);
			undoHandler.setHandler(setBlockHandler);
			undoHandler.saveBlocks(positions);
			for (BlockPos pos1 : positions) {
				setBlockHandler.setBlock(pos1, blockState);
			}
		}
	}
}