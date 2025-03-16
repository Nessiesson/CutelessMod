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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandFlip extends ClientCommand {

	@Override
	public String getName() {
		return "flip";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.flip.usage").getUnformattedText();
	}

	private void flipSelection(World world, WorldEditSelection selection) {
		EnumFacing direction = WorldEdit.getLookingDirection();
		Map<BlockPos, IBlockState> blockList = new HashMap<>();
		for (int x = 0; x < selection.widthX(); x++) {
			for (int y = 0; y < selection.widthY(); y++) {
				for (int z = 0; z < selection.widthZ(); z++) {
					if (Thread.interrupted()) {
						return;
					}
					IBlockState blockState = world.getBlockState(selection.minPos().add(x, y, z));
					BlockPos target;
					if (direction.getAxis() == EnumFacing.Axis.Y) {
						target = selection.minPos().add(x, selection.widthY() - y - 1, z);
					} else if (direction.getAxis() == EnumFacing.Axis.Z) {
						target = selection.minPos().add(x, y, selection.widthZ() - z - 1);
					} else {
						target = selection.minPos().add(selection.widthX() - x - 1, y, z);
					}
					blockList.put(target,
						WorldEdit.flipBlockstate(blockState, direction.getAxis()));
				}
			}
		}
		TaskSetBlock task = new TaskSetBlock(blockList, world);
		TaskManager.getInstance().addTask(task);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (args.length == 0) {
			if (WorldEdit.hasCurrentSelection()) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				World world = sender.getEntityWorld();
				Thread t = new Thread(() -> flipSelection(world, selection));
				t.start();
				TaskManager.getInstance().threads.add(t);
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation(
					"text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
			}
		} else {
			WorldEdit.sendMessage(getUsage(sender));
		}
	}
}