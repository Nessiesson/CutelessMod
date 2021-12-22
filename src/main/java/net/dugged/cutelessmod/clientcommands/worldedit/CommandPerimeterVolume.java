package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandPerimeterVolume extends ClientCommand {

	public CommandPerimeterVolume() {
		creativeOnly = false;
	}

	@Override
	public String getName() {
		return "perimetervolume";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.perimetervolume.usage").getUnformattedText();
	}

	private void countBlocks(World world, BlockPos startPos, int radius, boolean speedy) {
		List<BlockPos> checkedBlocks = new ArrayList<>();
		List<BlockPos> blocksToCheck = new ArrayList<>();
		BlockPos pos1;
		blocksToCheck.add(startPos);
		checkedBlocks.add(startPos);
		int count = 0;
		int i = 0;
		while (blocksToCheck.size() > 0) {
			if (Thread.interrupted()) {
				return;
			}
			if (speedy) {
				i = blocksToCheck.size() - 1;
			}
			BlockPos pos = blocksToCheck.get(i);
			pos1 = pos.north();
			if (world.isBlockLoaded(pos) && world.getBlockState(pos1).getBlock() instanceof BlockAir && WorldEdit.checkCircle(pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
				if (!checkedBlocks.contains(pos1)) {
					blocksToCheck.add(pos1);
					checkedBlocks.add(pos1);
				}
			}
			pos1 = pos.east();
			if (world.isBlockLoaded(pos) && world.getBlockState(pos1).getBlock() instanceof BlockAir && WorldEdit.checkCircle(pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
				if (!checkedBlocks.contains(pos1)) {
					blocksToCheck.add(pos1);
					checkedBlocks.add(pos1);
				}
			}
			pos1 = pos.south();
			if (world.isBlockLoaded(pos) && world.getBlockState(pos1).getBlock() instanceof BlockAir && WorldEdit.checkCircle(pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
				if (!checkedBlocks.contains(pos1)) {
					blocksToCheck.add(pos1);
					checkedBlocks.add(pos1);
				}
			}
			pos1 = pos.west();
			if (world.isBlockLoaded(pos) && world.getBlockState(pos1).getBlock() instanceof BlockAir && WorldEdit.checkCircle(pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
				if (!checkedBlocks.contains(pos1)) {
					blocksToCheck.add(pos1);
					checkedBlocks.add(pos1);
				}
			}
			WorldEditRenderer.bbToRender.add(new WorldEditRenderer.RenderedBB(pos, new BlockPos(pos.getX(), 0, pos.getZ()), 4));
			while (pos.getY() >= 0) {
				final IBlockState block = world.getBlockState(pos);
				if (block.isNormalCube() && !block.getBlock().equals(Blocks.BEDROCK)) {
					count++;
				}
				pos = pos.down();
			}
			blocksToCheck.remove(i);
		}
		WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.perimetervolume.response", count));
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0 || args.length == 1 || args.length == 2) {
			World world = sender.getEntityWorld();
			BlockPos pos = WorldEdit.playerPos();
			if (world.getBlockState(pos).getBlock() instanceof BlockAir) {
				int radius;
				boolean speedy;
				if (args.length >= 1) {
					radius = parseInt(args[0]);
				} else {
					radius = 100;
				}
				if (args.length == 2) {
					speedy = parseBoolean(args[1]);
				} else {
					speedy = false;
				}
				Thread t = new Thread(() -> countBlocks(world, pos, radius, speedy));
				t.start();
				ClientCommandHandler.instance.threads.add(t);
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.floodfill.noSpaceToFlood"));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.perimetervolume.usage"));
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
