package net.dugged.cutelessmod.clientcommands.worldedit;

import java.util.HashMap;
import java.util.Map;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.TaskManager;
import net.dugged.cutelessmod.clientcommands.TaskSetBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandUpscale extends ClientCommand {

	@Override
	public String getName() {
		return "upscale";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.upscale.usage").getUnformattedText();
	}

	private void upscaleSelection(World world, WorldEditSelection selection, int factor) {
		if (factor <= 0) {
			factor = 1;
		}
		Map<BlockPos, IBlockState> blocksToPlace = new HashMap<>();
		BlockPos base = selection.minPos();
		int widthX = selection.widthX();
		int widthY = selection.widthY();
		int widthZ = selection.widthZ();
		for (int x = 0; x < widthX; x++) {
			for (int y = 0; y < widthY; y++) {
				for (int z = 0; z < widthZ; z++) {
					if (Thread.interrupted()) {
						return;
					}
					IBlockState blockState = world.getBlockState(base.add(x, y, z));
					BlockPos scaledMin = new BlockPos(base.getX() + (x * factor),
						base.getY() + (y * factor), base.getZ() + (z * factor));
					BlockPos scaledMax = new BlockPos(scaledMin.getX() + factor - 1,
						scaledMin.getY() + factor - 1, scaledMin.getZ() + factor - 1);
					boolean skip = true;
					for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(scaledMin,
						scaledMax)) {
						if (!world.getBlockState(pos).equals(blockState)) {
							skip = false;
							break;
						}
					}
					if (!skip) {
						for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(scaledMin,
							scaledMax)) {
							blocksToPlace.put(pos, blockState);
						}
					}
				}
			}
		}
		TaskSetBlock task = new TaskSetBlock(blocksToPlace, world);
		TaskManager.getInstance().addTask(task);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (WorldEdit.hasCurrentSelection()) {
			WorldEditSelection selection = WorldEdit.getCurrentSelection();
			if (args.length == 1) {
				int factor = parseInt(args[0]);
				World world = sender.getEntityWorld();
				Thread t = new Thread(() -> upscaleSelection(world, selection, factor));
				t.start();
				TaskManager.getInstance().threads.add(t);
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation(
					"text.cutelessmod.clientcommands.worldEdit.upscale.usage"));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation(
				"text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}
}