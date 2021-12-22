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

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;

public class CommandCyl extends ClientCommand {
	@Override
	public String getName() {
		return "cyl";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.cyl.usage").getUnformattedText();
	}

	private void generateCyl(World world, WorldEditSelection selection, BlockPos center, IBlockState blockState, double radius, int height) {
		HandlerFill fillHandler = (HandlerFill) ClientCommandHandler.instance.createHandler(HandlerFill.class, world, selection);
		HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, world, selection);
		undoHandler.setHandler(fillHandler);
		if (height > world.getHeight() - center.getY()) {
			height = world.getHeight() - center.getY();
		}
		for (double x = 0; x <= radius; x++) {
			for (double z = 0; z <= radius; z++) {
				if (Thread.interrupted()) {
					return;
				}
				if (WorldEdit.checkCircle(x, z, radius)) {
					if (!WorldEdit.checkCircle(x + 1, z, radius) || !WorldEdit.checkCircle(x, z + 1, radius)) {
						undoHandler.saveBox(new BlockPos(center.getX() + x, center.getY(), center.getZ() - z), new BlockPos(center.getX() + x, center.getY() + height - 1, center.getZ() + z));
						undoHandler.saveBox(new BlockPos(center.getX() - x, center.getY(), center.getZ() - z), new BlockPos(center.getX() - x, center.getY() + height - 1, center.getZ() + z));
						fillHandler.fill(new BlockPos(center.getX() + x, center.getY(), center.getZ() - z), new BlockPos(center.getX() + x, center.getY() + height - 1, center.getZ() + z), blockState);
						fillHandler.fill(new BlockPos(center.getX() - x, center.getY(), center.getZ() - z), new BlockPos(center.getX() - x, center.getY() + height - 1, center.getZ() + z), blockState);
					}
				}
			}
		}
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length >= 3 && args.length <= 4) {
			if (WorldEdit.hasCurrentSelection()) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				if (selection.isOneByOne()) {
					World world = sender.getEntityWorld();
					Block block = getBlockByText(sender, args[0]);
					IBlockState blockState = convertArgToBlockState(block, args[1]);
					double radius = parseInt(args[2]) + 0.5;
					int height;
					if (args.length > 3) {
						height = Math.max(1, parseInt(args[3]));
					} else {
						height = 1;
					}
					Thread t = new Thread(() -> generateCyl(world, selection, selection.getPos(A), blockState, radius, height));
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
