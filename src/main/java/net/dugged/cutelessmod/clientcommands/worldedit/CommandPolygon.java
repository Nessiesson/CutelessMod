package net.dugged.cutelessmod.clientcommands.worldedit;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
import net.dugged.cutelessmod.clientcommands.HandlerUndo;
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

	private void generatePolygon(World world, WorldEditSelection selection, BlockPos center,
		IBlockState blockState, int radius, int points, boolean halfStep) {
		HandlerSetBlock setBlockHandler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(
			HandlerSetBlock.class, world, selection);
		List<BlockPos> undoBlockPositions = new ArrayList<>();
		HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(
			HandlerUndo.class, world, selection);
		undoHandler.setHandler(setBlockHandler);
		undoHandler.running = false;
		double rotation = Math.PI / points;
		if (halfStep) {
			rotation = Math.PI / (points * 2);
		}
		for (int i = 1; i < points + 1; i++) {
			if (Thread.interrupted()) {
				return;
			}
			int x1 = (int) (radius * Math.cos(2 * (-(Math.PI + rotation) + (Math.PI / points) * i))
				+ center.getX());
			int z1 = (int) (radius * Math.sin(2 * (-(Math.PI + rotation) + (Math.PI / points) * i))
				+ center.getZ());
			int x2 = (int) (
				radius * Math.cos(2 * (-(Math.PI + rotation) + (Math.PI / points) * (i + 1)))
					+ center.getX());
			int z2 = (int) (
				radius * Math.sin(2 * (-(Math.PI + rotation) + (Math.PI / points) * (i + 1)))
					+ center.getZ());
			int dx = Math.abs(x2 - x1);
			int dz = Math.abs(z2 - z1);
			int err, sx, sz;
			if (x1 > x2) {
				sx = -1;
			} else {
				sx = 1;
			}
			if (z1 > z2) {
				sz = -1;
			} else {
				sz = 1;
			}
			if (dx > dz) {
				err = dx / 2;
				while (x1 != x2) {
					if (Thread.interrupted()) {
						return;
					}
					undoBlockPositions.add(new BlockPos(x1, center.getY(), z1));
					setBlockHandler.setBlock(new BlockPos(x1, center.getY(), z1), blockState);
					err -= dz;
					if (err < 0) {
						z1 += sz;
						err += dx;
					}
					x1 += sx;
				}
			} else {
				err = dz / 2;
				while (z1 != z2) {
					if (Thread.interrupted()) {
						return;
					}
					undoBlockPositions.add(new BlockPos(x1, center.getY(), z1));
					setBlockHandler.setBlock(new BlockPos(x1, center.getY(), z1), blockState);
					err -= dx;
					if (err < 0) {
						x1 += sx;
						err += dz;
					}
					z1 += sz;
				}
			}
		}
		undoHandler.saveBlocks(undoBlockPositions);
		undoHandler.running = true;
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
					boolean halfStep;
					if (args.length == 5) {
						halfStep = parseBoolean(args[4]);
					} else {
						halfStep = false;
					}
					Thread t = new Thread(
						() -> generatePolygon(world, selection, selection.getPos(A), blockState,
							radius, points, halfStep));
					t.start();
					ClientCommandHandler.instance.threads.add(t);
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
		} else if (args.length == 5) {
			return getListOfStringsMatchingLastWord(args, "true", "false");
		} else {
			return Collections.emptyList();
		}
	}
}
