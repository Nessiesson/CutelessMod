package net.dugged.cutelessmod.clientcommands.worldedit;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
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

public class CommandSphere extends ClientCommand {

	@Override
	public String getName() {
		return "sphere";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.sphere.usage").getUnformattedText();
	}

	private void generateSphere(World world, BlockPos center, IBlockState blockState,
		double radius) {
		HandlerFill fillHandler = (HandlerFill) ClientCommandHandler.instance.createHandler(
			HandlerFill.class, world, null);
		HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(
			HandlerUndo.class, world, null);
		undoHandler.setHandler(fillHandler);
		undoHandler.running = false;
		for (double x = 0; x <= radius; x++) {
			for (double y = 0; y <= Math.min(radius, world.getHeight() - center.getY()); y++) {
				for (double z = 0; z <= radius; z++) {
					if (Thread.interrupted()) {
						return;
					}
					if (WorldEdit.checkSphere(x, y, z, radius)) {
						if (!WorldEdit.checkSphere(x + 1, y, z, radius) || !WorldEdit.checkSphere(x,
							y + 1, z, radius) || !WorldEdit.checkSphere(x, y, z + 1, radius)) {
							undoHandler.saveBox(new BlockPos(center.getX() + x, center.getY() + y,
									center.getZ() + z),
								new BlockPos(center.getX() + x, Math.max(0, center.getY() - y),
									center.getZ() + z));
							fillHandler.fill(new BlockPos(center.getX() + x, center.getY() + y,
									center.getZ() + z),
								new BlockPos(center.getX() + x, Math.max(0, center.getY() - y),
									center.getZ() + z), blockState);
							undoHandler.saveBox(new BlockPos(center.getX() + x, center.getY() + y,
									center.getZ() - z),
								new BlockPos(center.getX() + x, Math.max(0, center.getY() - y),
									center.getZ() - z));
							fillHandler.fill(new BlockPos(center.getX() + x, center.getY() + y,
									center.getZ() - z),
								new BlockPos(center.getX() + x, Math.max(0, center.getY() - y),
									center.getZ() - z), blockState);
							undoHandler.saveBox(new BlockPos(center.getX() - x, center.getY() + y,
									center.getZ() + z),
								new BlockPos(center.getX() - x, Math.max(0, center.getY() - y),
									center.getZ() + z));
							fillHandler.fill(new BlockPos(center.getX() - x, center.getY() + y,
									center.getZ() + z),
								new BlockPos(center.getX() - x, Math.max(0, center.getY() - y),
									center.getZ() + z), blockState);
							undoHandler.saveBox(new BlockPos(center.getX() - x, center.getY() + y,
									center.getZ() - z),
								new BlockPos(center.getX() - x, Math.max(0, center.getY() - y),
									center.getZ() - z));
							fillHandler.fill(new BlockPos(center.getX() - x, center.getY() + y,
									center.getZ() - z),
								new BlockPos(center.getX() - x, Math.max(0, center.getY() - y),
									center.getZ() - z), blockState);
						}
					}
				}
			}
		}
		undoHandler.running = true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (args.length == 3) {
			if (WorldEdit.hasCurrentSelection()) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				if (selection.isOneByOne()) {
					Block block = getBlockByText(sender, args[0]);
					IBlockState blockState = convertArgToBlockState(block, args[1]);
					World world = sender.getEntityWorld();
					double radius = parseInt(args[2]) + 0.5;
					Thread t = new Thread(
						() -> generateSphere(world, selection.getPos(A), blockState, radius));
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
		} else {
			return Collections.emptyList();
		}
	}
}
