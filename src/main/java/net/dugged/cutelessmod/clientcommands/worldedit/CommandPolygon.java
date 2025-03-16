package net.dugged.cutelessmod.clientcommands.worldedit;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.TaskManager;
import net.dugged.cutelessmod.clientcommands.TaskSetBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandPolygon extends ClientCommand {

	@Override
	public String getName() {
		return "polygon";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.polygon.usage").getUnformattedText();
	}

	private List<BlockPos> getLine(BlockPos start, BlockPos end) {
		List<BlockPos> line = new ArrayList<>();
		int x1 = start.getX(), z1 = start.getZ();
		int x2 = end.getX(), z2 = end.getZ();
		int dx = Math.abs(x2 - x1);
		int dz = Math.abs(z2 - z1);
		int sx = (x1 < x2) ? 1 : -1;
		int sz = (z1 < z2) ? 1 : -1;
		int err = dx - dz;
		while (true) {
			line.add(new BlockPos(x1, start.getY(), z1));
			if (x1 == x2 && z1 == z2) {
				break;
			}
			int e2 = 2 * err;
			if (e2 > -dz) {
				err -= dz;
				x1 += sx;
			}
			if (e2 < dx) {
				err += dx;
				z1 += sz;
			}
		}
		return line;
	}

	private List<BlockPos> generateVertices(BlockPos center, int radius, int points,
		boolean halfStep) {
		List<BlockPos> vertices = new ArrayList<>();
		double angleStep = 2 * Math.PI / points;
		double startAngle = halfStep ? angleStep / 2.0 : 0;
		for (int i = 0; i < points; i++) {
			double angle = startAngle + i * angleStep;
			int x = center.getX() + (int) Math.round(radius * Math.cos(angle));
			int z = center.getZ() + (int) Math.round(radius * Math.sin(angle));
			vertices.add(new BlockPos(x, center.getY(), z));
		}
		return vertices;
	}

	private void generatePolygon(World world, WorldEditSelection selection, BlockPos center,
		IBlockState blockState, int radius, int points, boolean halfStep) {
		List<BlockPos> vertices = generateVertices(center, radius, points, halfStep);
		Map<BlockPos, IBlockState> blocksToPlace = new HashMap<>();
		// Connect each vertex to the next, wrapping around to the first.
		for (int i = 0; i < vertices.size(); i++) {
			BlockPos start = vertices.get(i);
			BlockPos end = vertices.get((i + 1) % vertices.size());
			for (BlockPos pos : getLine(start, end)) {
				blocksToPlace.put(pos, blockState);
			}
		}
		TaskSetBlock task = new TaskSetBlock(blocksToPlace, world);
		TaskManager.getInstance().addTask(task);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (args.length >= 4 && args.length <= 5) {
			if (WorldEdit.hasCurrentSelection()) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				if (selection.isOneByOne()) {
					Block block = getBlockByText(sender, args[0]);
					IBlockState blockState = convertArgToBlockState(block, args[1]);
					World world = sender.getEntityWorld();
					int radius = parseInt(args[2]);
					int points = parseInt(args[3]);
					boolean halfStep = args.length == 5 && parseBoolean(args[4]);
					Thread t = new Thread(() ->
						generatePolygon(world, selection, selection.getPos(A), blockState, radius,
							points, halfStep)
					);
					t.start();
					TaskManager.getInstance().threads.add(t);
				} else {
					WorldEdit.sendMessage(new TextComponentTranslation(
						"text.cutelessmod.clientcommands.worldEdit.noOneByOneSelected"));
				}
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation(
					"text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
			}
		} else {
			WorldEdit.sendMessage(getUsage(sender));
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
		String[] args, @Nullable BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
		} else if (args.length == 5) {
			return getListOfStringsMatchingLastWord(args, "true", "false");
		} else {
			return Collections.emptyList();
		}
	}
}