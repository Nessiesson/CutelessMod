package net.dugged.cutelessmod.clientcommands.worldedit;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

import java.util.ArrayList;
import java.util.List;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
import net.dugged.cutelessmod.clientcommands.HandlerUndo;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandErode extends ClientCommand {

	@Override
	public String getName() {
		return "erode";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.erode.usage").getUnformattedText();
	}

	public boolean isSurrounded(BlockPos pos, World world, WorldEditSelection selection) {
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					if (x == 0 && y == 0 && z == 0) {
						continue;
					}
					BlockPos pos1 = pos.add(x, y, z);
					if (selection.minPos().getX() == pos1.getX() ||
						selection.minPos().getY() == pos1.getY() ||
						selection.minPos().getZ() == pos1.getZ() ||
						selection.maxPos().getX() == pos1.getX() ||
						selection.maxPos().getY() == pos1.getY() ||
						selection.maxPos().getZ() == pos1.getZ()) {
						continue;
					}
					if (!world.getBlockState(pos1).isFullBlock()) {
						return false;
					}
				}
			}

		}
		return true;
	}

	public void erode(World world, WorldEditSelection selection) {
		HandlerSetBlock setBlockHandler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world, selection);
		setBlockHandler.running = false;
		List<BlockPos> undoBlockPositions = new ArrayList<>();
		HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, world, selection);
		undoHandler.setHandler(setBlockHandler);
		undoHandler.running = false;
		for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(selection.getPos(A), selection.getPos(B))) {
			if (Thread.interrupted()) {
				return;
			}
			if (world.isAirBlock(pos) || !isSurrounded(pos, world, selection)) {
				continue;
			}
			undoBlockPositions.add(pos);
			setBlockHandler.setBlock(pos, Blocks.AIR.getDefaultState());
		}
		undoHandler.saveBlocks(undoBlockPositions);
		undoHandler.running = true;
	}

	@Override
	public void execute(
		MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			if (WorldEdit.hasCurrentSelection()) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				final World world = sender.getEntityWorld();
				Thread t = new Thread(() -> erode(world, selection));
				t.start();
				ClientCommandHandler.instance.threads.add(t);
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
			}
		} else {
			WorldEdit.sendMessage(getUsage(sender));
		}
	}
}
