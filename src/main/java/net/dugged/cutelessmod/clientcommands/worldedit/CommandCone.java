package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerFill;
import net.dugged.cutelessmod.clientcommands.HandlerUndo;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandCone extends ClientCommand {
	@Override
	public String getName() {
		return "cone";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.cone.usage").getUnformattedText();
	}

	private void generateCircle(HandlerFill fillHandler, HandlerUndo undoHandler, BlockPos center, IBlockState blockState, double radius) {
		for (double x = 0; x <= radius; x++) {
			for (double z = 0; z <= radius; z++) {
				if (Thread.interrupted()) {
					return;
				}
				if (WorldEdit.checkCircle(x, z, radius)) {
					if (!WorldEdit.checkCircle(x + 1, z, radius) || !WorldEdit.checkCircle(x, z + 1, radius)) {
						undoHandler.saveBox(new BlockPos(center.getX() + x, center.getY(), center.getZ() - z), new BlockPos(center.getX() + x, center.getY(), center.getZ() + z));
						undoHandler.saveBox(new BlockPos(center.getX() - x, center.getY(), center.getZ() - z), new BlockPos(center.getX() - x, center.getY(), center.getZ() + z));
						fillHandler.fill(new BlockPos(center.getX() + x, center.getY(), center.getZ() - z), new BlockPos(center.getX() + x, center.getY(), center.getZ() + z), blockState);
						fillHandler.fill(new BlockPos(center.getX() - x, center.getY(), center.getZ() - z), new BlockPos(center.getX() - x, center.getY(), center.getZ() + z), blockState);
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

	private void generateCone(World world, WorldEditSelection selection, IBlockState blockState, int startRadius, int endRadius, int height, double bezierA, double bezierB) {
		HandlerFill fillHandler = (HandlerFill) ClientCommandHandler.instance.createHandler(HandlerFill.class, world, selection);
		HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, world, selection);
		undoHandler.setHandler(fillHandler);
		undoHandler.running = false;
		generateCircle(fillHandler, undoHandler, selection.minPos().down(height - 1), blockState, startRadius);
		for (int i = 1; i <= height; i++) {
			if (Thread.interrupted()) {
				return;
			}
			double t = (1.0D / height) * i;
			generateCircle(fillHandler, undoHandler, selection.minPos().down(height - i), blockState, calculateBezierValue(startRadius, endRadius, bezierA, bezierB, t));
		}
		undoHandler.running = true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
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
					Thread t = new Thread(() -> generateCone(world, selection, blockState, startRadius, endRadius, height, bezierA, bezierB));
					t.start();
					ClientCommandHandler.instance.threads.add(t);
				} else {
					WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noOneByOneSelected"));
				}
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
			}
		} else {
			WorldEdit.sendMessage(getUsage(sender));
		}
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
		} else {
			return Collections.emptyList();
		}
	}
}
