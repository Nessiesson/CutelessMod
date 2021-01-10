package net.dugged.cutelessmod.clientcommands.worldedit;

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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandHSphere extends ClientCommand {

	@Override
	public String getName() {
		return "hsphere";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.hsphere.usage").getUnformattedText();
	}

	private void generateHollowSphere(World world, IBlockState blockstate, double radius) {
		HandlerSetBlock setBlockHandler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world);
		List<BlockPos> undoBlockPositions = new ArrayList<>();
		HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, world);
		undoHandler.setHandler(setBlockHandler);
		undoHandler.running = false;
		for (double x = 0; x <= radius; x++) {
			for (double y = 0; y <= Math.min(radius, world.getHeight() - WorldEdit.posA.getY()); y++) {
				for (double z = 0; z <= radius; z++) {
					if (WorldEdit.checkSphere(x, y, z, radius)) {
						if (!WorldEdit.checkSphere(x + 1, y, z, radius) || !WorldEdit.checkSphere(x, y + 1, z, radius) || !WorldEdit.checkSphere(x, y, z + 1, radius)) {
							undoBlockPositions.add(new BlockPos(WorldEdit.posA.getX() + x, WorldEdit.posA.getY() + y, WorldEdit.posA.getZ() + z));
							setBlockHandler.setBlock(new BlockPos(WorldEdit.posA.getX() + x, WorldEdit.posA.getY() + y, WorldEdit.posA.getZ() + z), blockstate);
							undoBlockPositions.add(new BlockPos(WorldEdit.posA.getX() + x, WorldEdit.posA.getY() + y, WorldEdit.posA.getZ() - z));
							setBlockHandler.setBlock(new BlockPos(WorldEdit.posA.getX() + x, WorldEdit.posA.getY() + y, WorldEdit.posA.getZ() - z), blockstate);
							undoBlockPositions.add(new BlockPos(WorldEdit.posA.getX() - x, WorldEdit.posA.getY() + y, WorldEdit.posA.getZ() + z));
							setBlockHandler.setBlock(new BlockPos(WorldEdit.posA.getX() - x, WorldEdit.posA.getY() + y, WorldEdit.posA.getZ() + z), blockstate);
							undoBlockPositions.add(new BlockPos(WorldEdit.posA.getX() - x, WorldEdit.posA.getY() + y, WorldEdit.posA.getZ() - z));
							setBlockHandler.setBlock(new BlockPos(WorldEdit.posA.getX() - x, WorldEdit.posA.getY() + y, WorldEdit.posA.getZ() - z), blockstate);
							if (WorldEdit.posA.getY() + y >= 0) {
								undoBlockPositions.add(new BlockPos(WorldEdit.posA.getX() + x, WorldEdit.posA.getY() - y, WorldEdit.posA.getZ() - z));
								setBlockHandler.setBlock(new BlockPos(WorldEdit.posA.getX() + x, WorldEdit.posA.getY() - y, WorldEdit.posA.getZ() - z), blockstate);
								undoBlockPositions.add(new BlockPos(WorldEdit.posA.getX() + x, WorldEdit.posA.getY() - y, WorldEdit.posA.getZ() + z));
								setBlockHandler.setBlock(new BlockPos(WorldEdit.posA.getX() + x, WorldEdit.posA.getY() - y, WorldEdit.posA.getZ() + z), blockstate);
								undoBlockPositions.add(new BlockPos(WorldEdit.posA.getX() - x, WorldEdit.posA.getY() - y, WorldEdit.posA.getZ() + z));
								setBlockHandler.setBlock(new BlockPos(WorldEdit.posA.getX() - x, WorldEdit.posA.getY() - y, WorldEdit.posA.getZ() + z), blockstate);
								undoBlockPositions.add(new BlockPos(WorldEdit.posA.getX() - x, WorldEdit.posA.getY() - y, WorldEdit.posA.getZ() - z));
								setBlockHandler.setBlock(new BlockPos(WorldEdit.posA.getX() - x, WorldEdit.posA.getY() - y, WorldEdit.posA.getZ() - z), blockstate);
							}
						}
					}
				}
			}
		}
		undoHandler.saveBlocks(undoBlockPositions);
		undoHandler.running = true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 3) {
			if (WorldEdit.hasSelection() && WorldEdit.isOneByOne()) {
				Block block = getBlockByText(sender, args[0]);
				IBlockState blockstate = convertArgToBlockState(block, args[1]);
				World world = sender.getEntityWorld();
				double radius = parseInt(args[2]) + 0.5;
				Thread t = new Thread(() -> generateHollowSphere(world, blockstate, radius));
				t.start();
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noOneByOneSelected"));
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
