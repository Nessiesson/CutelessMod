package net.dugged.cutelessmod.clientcommands.worldedit;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.TaskFill;
import net.dugged.cutelessmod.clientcommands.TaskManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandCone extends ClientCommand {

	@Override
	public String getName() {
		return "cone";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.cone.usage").getUnformattedText();
	}

	private void generateCircle(Map<AxisAlignedBB, IBlockState> regionMap, BlockPos center,
		IBlockState blockState, double radius) {
		for (double x = 0; x <= radius; x++) {
			for (double z = 0; z <= radius; z++) {
				if (Thread.interrupted()) {
					return;
				}
				if (WorldEdit.checkCircle(x, z, radius)) {
					if (!WorldEdit.checkCircle(x + 1, z, radius) || !WorldEdit.checkCircle(x, z + 1,
						radius)) {
						BlockPos pos1 = new BlockPos(center.getX() + (int) x, center.getY(),
							center.getZ() - (int) z);
						BlockPos pos2 = new BlockPos(center.getX() + (int) x, center.getY(),
							center.getZ() + (int) z);
						AxisAlignedBB region1 = new AxisAlignedBB(pos1, pos2.add(1, 1, 1));
						regionMap.put(region1, blockState);
						BlockPos pos3 = new BlockPos(center.getX() - (int) x, center.getY(),
							center.getZ() - (int) z);
						BlockPos pos4 = new BlockPos(center.getX() - (int) x, center.getY(),
							center.getZ() + (int) z);
						AxisAlignedBB region2 = new AxisAlignedBB(pos3, pos4.add(1, 1, 1));
						regionMap.put(region2, blockState);
					}
				}
			}
		}
	}

	public int calculateBezierValue(int start, int end, double pointA, double pointB, double t) {
		double u = 1.0 - t;
		double tt = t * t;
		double uu = u * u;
		double ttt = tt * t;
		double x = 3 * uu * t * pointA + 3 * u * tt * pointB + ttt;
		return (int) (start + (end - start) * x);
	}

	private void generateCone(World world, WorldEditSelection selection, IBlockState blockState,
		int startRadius, int endRadius, int height, double bezierA, double bezierB) {
		Map<AxisAlignedBB, IBlockState> regionMap = new LinkedHashMap<>();
		generateCircle(regionMap, selection.minPos().down(height - 1), blockState, startRadius);
		for (int i = 1; i <= height; i++) {
			if (Thread.interrupted()) {
				return;
			}
			double t = (1.0D / height) * i;
			int currentRadius = calculateBezierValue(startRadius, endRadius, bezierA, bezierB, t);
			generateCircle(regionMap, selection.minPos().down(height - i), blockState,
				currentRadius);
		}
		TaskFill task = new TaskFill(regionMap, world);
		TaskManager.getInstance().addTask(task);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (args.length == 7) {
			if (WorldEdit.hasCurrentSelection()) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				if (selection.isOneByOne()) {
					World world = sender.getEntityWorld();
					Block block = getBlockByText(sender, args[0]);
					IBlockState blockState = convertArgToBlockState(block, args[1]);
					int startRadius = Math.max(1, parseInt(args[2]));
					int endRadius = Math.max(1, parseInt(args[3]));
					int height = Math.max(1, parseInt(args[4]));
					double bezierA = parseDouble(args[5]);
					double bezierB = parseDouble(args[6]);
					Thread t = new Thread(
						() -> generateCone(world, selection, blockState, startRadius, endRadius,
							height, bezierA, bezierB));
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

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
		String[] args, @Nullable BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
		} else {
			return Collections.emptyList();
		}
	}
}