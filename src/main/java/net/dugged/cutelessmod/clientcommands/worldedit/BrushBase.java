package net.dugged.cutelessmod.clientcommands.worldedit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.dugged.cutelessmod.clientcommands.TaskManager;
import net.dugged.cutelessmod.clientcommands.TaskSetBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
		List<BlockPos> positions = run(world, pos);
		Map<BlockPos, IBlockState> blocksToPlace = positions.stream()
			.collect(Collectors.toMap(Function.identity(), p -> blockState));
		if (!blocksToPlace.isEmpty()) {
			TaskManager.getInstance().addTask(new TaskSetBlock(blocksToPlace, world));
		}
	}

}