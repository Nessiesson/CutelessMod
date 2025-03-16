package net.dugged.cutelessmod.clientcommands.worldedit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerFill;
import net.dugged.cutelessmod.clientcommands.HandlerUndo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandOutlineFill extends ClientCommand {

	@Override
	public String getName() {
		return "outlinefill";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.outlinefill.usage").getUnformattedText();
	}

	private void outLineFill(World world, IBlockState blockState, BlockPos startPos, int height,
		int radius) {
		HandlerFill fillHandler = (HandlerFill) ClientCommandHandler.instance.createHandler(
			HandlerFill.class, world, null);
		HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(
			HandlerUndo.class, world, null);
		undoHandler.setHandler(fillHandler);
		undoHandler.running = false;
		List<BlockPos> checkedBlocks = new ArrayList<>();
		List<BlockPos> blocksToCheck = new ArrayList<>();
		BlockPos pos1;
		blocksToCheck.add(startPos);
		checkedBlocks.add(startPos);
		while (!blocksToCheck.isEmpty()) {
			if (Thread.interrupted()) {
				return;
			}
			BlockPos pos = blocksToCheck.get(0);
			pos1 = pos.north();
			if (world.getBlockState(pos1).getBlock() instanceof BlockAir && WorldEdit.checkCircle(
				pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
				if (!checkedBlocks.contains(pos1)) {
					blocksToCheck.add(pos1);
					checkedBlocks.add(pos1);
				}
			}
			pos1 = pos.east();
			if (world.getBlockState(pos1).getBlock() instanceof BlockAir && WorldEdit.checkCircle(
				pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
				if (!checkedBlocks.contains(pos1)) {
					blocksToCheck.add(pos1);
					checkedBlocks.add(pos1);
				}
			}
			pos1 = pos.south();
			if (world.getBlockState(pos1).getBlock() instanceof BlockAir && WorldEdit.checkCircle(
				pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
				if (!checkedBlocks.contains(pos1)) {
					blocksToCheck.add(pos1);
					checkedBlocks.add(pos1);
				}
			}
			pos1 = pos.west();
			if (world.getBlockState(pos1).getBlock() instanceof BlockAir && WorldEdit.checkCircle(
				pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
				if (!checkedBlocks.contains(pos1)) {
					blocksToCheck.add(pos1);
					checkedBlocks.add(pos1);
				}
			}
			WorldEditRenderer.bbToRender.add(new WorldEditRenderer.RenderedBB(pos,
				new BlockPos(pos.getX(), Math.max(pos.getY() + height, 0), pos.getZ()), 4, 255, 0,
				0));
			undoHandler.saveBox(pos,
				new BlockPos(pos.getX(), Math.max(pos.getY() + height, 0), pos.getZ()));
			fillHandler.fill(pos,
				new BlockPos(pos.getX(), Math.max(pos.getY() + height, 0), pos.getZ()), blockState);
			blocksToCheck.remove(0);
		}
		undoHandler.running = true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (args.length == 3 || args.length == 4) {
			World world = sender.getEntityWorld();
			BlockPos pos = WorldEdit.playerPos();
			if (world.getBlockState(pos).getBlock() instanceof BlockAir) {
				Block block = getBlockByText(sender, args[0]);
				IBlockState blockState = convertArgToBlockState(block, args[1]);
				int height = parseInt(args[2]);
				int radius;
				if (args.length == 4) {
					radius = parseInt(args[3]);
				} else {
					radius = 100;
				}
				Thread t = new Thread(() -> outLineFill(world, blockState, pos, height, radius));
				t.start();
				ClientCommandHandler.instance.threads.add(t);
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation(
					"text.cutelessmod.clientcommands.worldEdit.floodfill.noSpaceToFlood"));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation(
				"text.cutelessmod.clientcommands.worldEdit.outlinefill.usage"));
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
