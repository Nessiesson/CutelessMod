package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public class CommandRandomize extends ClientCommand {

	@Override
	public String getName() {
		return "randomize";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.randomize.usage").getUnformattedText();
	}

	private List<IBlockState> parseBlockList(String[] argList, ICommandSender sender) throws CommandException {
		List<IBlockState> blockList = new ArrayList<IBlockState>();
		for (String args : String.join(" ", argList).split(",")) {
			String[] arg = args.trim().split(" ");
			if (arg.length <= 2) {
				Block block = getBlockByText(sender, arg[0]);
				IBlockState iblockstate = block.getDefaultState();
				if (arg.length == 2) {
					iblockstate = convertArgToBlockState(block, arg[1]);
				}
				blockList.add(iblockstate);
			}
		}
		return blockList;
	}

	private void placeRandomBlocks(World world, List<IBlockState> blockList, int percentage) {
		HandlerSetBlock handler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world);
		handler.autoCancel = false;
		Random rand = new Random();
		for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(WorldEdit.minPos(), WorldEdit.maxPos())) {
			if (rand.nextFloat() <= (float) percentage / 100F) {
				IBlockState blockState = blockList.get(rand.nextInt(blockList.size()));
				handler.setBlock(pos, blockState);
			}
		}
		handler.autoCancel = true;
		handler.sendAffectedBlocks = true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length >= 2) {
			if (WorldEdit.hasSelection()) {
				sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
				int percentage = parseInt(args[0], 0, 100);
				World world = sender.getEntityWorld();
				List<IBlockState> blockList = parseBlockList(Arrays.copyOfRange(args, 1, args.length), sender);
				Thread t = new Thread(() -> placeRandomBlocks(world, blockList, percentage));
				t.start();
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
