package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
import net.dugged.cutelessmod.clientcommands.HandlerUndo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommandFlip extends ClientCommand {

	@Override
	public String getName() {
		return "flip";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.flip.usage").getUnformattedText();
	}

	private void flipSelection(World world, WorldEditSelection selection) {
		HandlerSetBlock setBlockHandler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world, selection);
		HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, world, selection);
		undoHandler.setHandler(setBlockHandler);
		undoHandler.running = false;
		EnumFacing direction = WorldEdit.getLookingDirection();
		Map<BlockPos, IBlockState> blockList = new HashMap<>();
		for (int x = 0; x < selection.widthX(); x++) {
			for (int y = 0; y < selection.widthY(); y++) {
				for (int z = 0; z < selection.widthZ(); z++) {
					IBlockState blockState;
					if (direction.getAxis() == EnumFacing.Axis.Y) {
						blockState = world.getBlockState(selection.minPos().add(x, y, z));
						blockList.put(selection.minPos().add(x, selection.widthY() - y - 1, z), WorldEdit.flipBlockstate(blockState, direction.getAxis()));
					} else if (direction.getAxis() == EnumFacing.Axis.Z) {
						blockState = world.getBlockState(selection.minPos().add(x, y, z));
						blockList.put(selection.minPos().add(x, y, selection.widthZ() - z - 1), WorldEdit.flipBlockstate(blockState, direction.getAxis()));
					} else {
						blockState = world.getBlockState(selection.minPos().add(x, y, z));
						blockList.put(selection.minPos().add(selection.widthX() - x - 1, y, z), WorldEdit.flipBlockstate(blockState, direction.getAxis()));
					}
				}
			}
		}
		undoHandler.saveBlocks(new ArrayList<>(blockList.keySet()));
		setBlockHandler.setBlocks(blockList);
		undoHandler.running = true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			if (WorldEdit.hasCurrentSelection()) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				World world = sender.getEntityWorld();
				Thread t = new Thread(() -> flipSelection(world, selection));
				t.start();
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
			}
		} else {
			WorldEdit.sendMessage(getUsage(sender));
		}
	}
}
