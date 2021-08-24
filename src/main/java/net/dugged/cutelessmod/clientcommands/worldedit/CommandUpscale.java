package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerFill;
import net.dugged.cutelessmod.clientcommands.HandlerUndo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class CommandUpscale extends ClientCommand {
	@Override
	public String getName() {
		return "upscale";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.upscale.usage").getUnformattedText();
	}

	private void upscaleSelection(World world, WorldEditSelection selection, int factor) {
		Map<BlockPos, IBlockState> blockList = new HashMap<>();
		if (factor <= 0) {
			factor = 1;
		}
		for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(selection.minPos(), selection.maxPos())) {
			blockList.put(pos, world.getBlockState(pos));
		}
		HandlerFill fillHandler = (HandlerFill) ClientCommandHandler.instance.createHandler(HandlerFill.class, world, selection);
		HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, world, selection);
		undoHandler.setHandler(fillHandler);
		undoHandler.running = false;
		for (int x = 0; x < selection.widthX(); x++) {
			for (int y = 0; y < selection.widthY(); y++) {
				for (int z = 0; z < selection.widthZ(); z++) {
					IBlockState blockState = world.getBlockState(new BlockPos(selection.minPos().getX() + x, selection.minPos().getY() + y, selection.minPos().getZ() + z));
					BlockPos minPos = new BlockPos(selection.minPos().getX() + (x * factor), selection.minPos().getY() + (y * factor), selection.minPos().getZ() + (z * factor));
					BlockPos maxPos = new BlockPos(minPos.getX() + (factor - 1), minPos.getY() + (factor - 1), minPos.getZ() + (factor - 1));
					boolean skip = true;
					for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(minPos, maxPos)) {
						if (world.getBlockState(pos) != blockState) {
							skip = false;
							break;
						}
					}
					if (!skip) {
						undoHandler.saveBox(minPos, maxPos);
						fillHandler.fill(minPos, maxPos, blockState);
					}
				}
			}
		}
		undoHandler.running = true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (WorldEdit.hasCurrentSelection()) {
			WorldEditSelection selection = WorldEdit.getCurrentSelection();
			if (args.length == 1) {
				int factor = parseInt(args[0]);
				World world = sender.getEntityWorld();
				Thread t = new Thread(() -> upscaleSelection(world, selection, factor));
				t.start();
			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}
}
