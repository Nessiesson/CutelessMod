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

	private void flipSelection(World world) {
		HandlerSetBlock setBlockHandler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world);
		HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, world);
		undoHandler.setHandler(setBlockHandler);
		undoHandler.running = false;
		EnumFacing direction = WorldEdit.getLookingDirection();
		Map<BlockPos, IBlockState> blockList = new HashMap<>();
		for (int x = 0; x < WorldEdit.widthX(); x++) {
			for (int y = 0; y < WorldEdit.widthY(); y++) {
				for (int z = 0; z < WorldEdit.widthZ(); z++) {
					IBlockState blockState;
					if (direction.getAxis() == EnumFacing.Axis.Y) {
						blockState = world.getBlockState(WorldEdit.minPos().add(x, y, z));
						blockList.put(WorldEdit.minPos().add(x, WorldEdit.widthY() - y - 1, z), WorldEdit.flipBlockstate(blockState, direction.getAxis()));
					} else if (direction.getAxis() == EnumFacing.Axis.Z) {
						blockState = world.getBlockState(WorldEdit.minPos().add(x, y, z));
						blockList.put(WorldEdit.minPos().add(x, y, WorldEdit.widthZ() - z - 1), WorldEdit.flipBlockstate(blockState, direction.getAxis()));
					} else {
						blockState = world.getBlockState(WorldEdit.minPos().add(x, y, z));
						blockList.put(WorldEdit.minPos().add(WorldEdit.widthX() - x - 1, y, z), WorldEdit.flipBlockstate(blockState, direction.getAxis()));
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
			if (WorldEdit.hasSelection()) {
				World world = sender.getEntityWorld();
				Thread t = new Thread(() -> flipSelection(world));
				t.start();
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
			}
		} else {
			WorldEdit.sendMessage(getUsage(sender));
		}
	}
}
