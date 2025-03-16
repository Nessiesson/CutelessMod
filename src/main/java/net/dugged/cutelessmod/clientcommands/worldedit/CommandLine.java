package net.dugged.cutelessmod.clientcommands.worldedit;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

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
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandLine extends ClientCommand {

	@Override
	public String getName() {
		return "line";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.line.usage").getUnformattedText();
	}

	private void placeLine(World world, WorldEditSelection selection, IBlockState blockState) {
		HandlerSetBlock setBlockHandler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(
			HandlerSetBlock.class, world, selection);
		List<BlockPos> undoBlockPositions = new ArrayList<>();
		HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(
			HandlerUndo.class, world, selection);
		undoHandler.setHandler(setBlockHandler);
		undoHandler.running = false;
		int x1 = selection.getPos(A).getX();
		int y1 = selection.getPos(A).getY();
		int z1 = selection.getPos(A).getZ();
		int x2 = selection.getPos(B).getX();
		int y2 = selection.getPos(B).getY();
		int z2 = selection.getPos(B).getZ();
		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);
		int dz = Math.abs(z2 - z1);
		int xs, ys, zs, p1, p2;
		undoBlockPositions.add(new BlockPos(x1, y1, z1));
		setBlockHandler.setBlock(new BlockPos(x1, y1, z1), blockState);
		if (x2 > x1) {
			xs = 1;
		} else {
			xs = -1;
		}
		if (y2 > y1) {
			ys = 1;
		} else {
			ys = -1;
		}
		if (z2 > z1) {
			zs = 1;
		} else {
			zs = -1;
		}
		if (dx >= dy && dx >= dz) {
			p1 = 2 * dy - dx;
			p2 = 2 * dz - dx;
			while (x1 != x2) {
				if (Thread.interrupted()) {
					return;
				}
				x1 += xs;
				if (p1 >= 0) {
					y1 += ys;
					p1 -= 2 * dx;
				}
				if (p2 >= 0) {
					z1 += zs;
					p2 -= 2 * dx;
				}
				p1 += 2 * dy;
				p2 += 2 * dz;
				undoBlockPositions.add(new BlockPos(x1, y1, z1));
				setBlockHandler.setBlock(new BlockPos(x1, y1, z1), blockState);
			}
		} else if (dy >= dx && dy >= dz) {
			p1 = 2 * dx - dy;
			p2 = 2 * dz - dy;
			while (y1 != y2) {
				if (Thread.interrupted()) {
					return;
				}
				y1 += ys;
				if (p1 >= 0) {
					x1 += xs;
					p1 -= 2 * dy;
				}
				if (p2 >= 0) {
					z1 += zs;
					p2 -= 2 * dy;
				}
				p1 += 2 * dx;
				p2 += 2 * dz;
				undoBlockPositions.add(new BlockPos(x1, y1, z1));
				setBlockHandler.setBlock(new BlockPos(x1, y1, z1), blockState);
			}
		} else {
			p1 = 2 * dy - dz;
			p2 = 2 * dx - dz;
			while (z1 != z2) {
				if (Thread.interrupted()) {
					return;
				}
				z1 += zs;
				if (p1 >= 0) {
					y1 += ys;
					p1 -= 2 * dz;
				}
				if (p2 >= 0) {
					x1 += xs;
					p2 -= 2 * dz;
				}
				p1 += 2 * dy;
				p2 += 2 * dx;
				undoBlockPositions.add(new BlockPos(x1, y1, z1));
				setBlockHandler.setBlock(new BlockPos(x1, y1, z1), blockState);
			}
		}
		undoHandler.saveBlocks(undoBlockPositions);
		undoHandler.running = true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (args.length <= 2) {
			if (WorldEdit.hasCurrentSelection()) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				Block block = Blocks.GLOWSTONE;
				if (args.length > 0) {
					block = getBlockByText(sender, args[0]);
				}
				IBlockState blockState;
				if (args.length >= 2) {
					blockState = convertArgToBlockState(block, args[1]);
				} else {
					blockState = block.getDefaultState();
				}
				World world = sender.getEntityWorld();
				Thread t = new Thread(() -> placeLine(world, selection, blockState));
				t.start();
				ClientCommandHandler.instance.threads.add(t);
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
