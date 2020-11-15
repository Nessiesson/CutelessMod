package net.dugged.cutelessmod.clientcommands;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public class CommandRandomize extends CommandBase {
	Random rand = new Random();

	@Override
	public String getName() {
		return "randomize";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.randomize.usage").toString();
	}

	private List<IBlockState> parseBlockList(String[] argList, ICommandSender sender) throws CommandException {
		List<IBlockState> blockList = new ArrayList<IBlockState>();
		for (String args : String.join(" ", argList).split(",")) {
			String[] arg = args.trim().split(" ");
			if (arg.length <= 2) {
				Block block = CommandBase.getBlockByText(sender, arg[0]);
				IBlockState iblockstate = block.getDefaultState();
				if (arg.length == 2) {
					iblockstate = convertArgToBlockState(block, arg[1]);
				}
				blockList.add(iblockstate);
			}
		}
		return blockList;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (sender instanceof EntityPlayerSP) {
			if (args.length < 8) {
				throw new WrongUsageException("text.cutelessmod.clientcommands.randomize.usage");
			} else {
				sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
				BlockPos blockpos = parseBlockPos(sender, args, 0, false);
				BlockPos blockpos1 = parseBlockPos(sender, args, 3, false);
				int percentage = parseInt(args[6], 0, 100);
				World world = sender.getEntityWorld();
				HandlerSetBlock setBlockHandler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world);
				List<IBlockState> blockList = parseBlockList(Arrays.copyOfRange(args, 7, args.length), sender);
				for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(blockpos, blockpos1)) {
					if (rand.nextFloat() <= (float) percentage / 100F) {
						IBlockState blockState = blockList.get(rand.nextInt(blockList.size()));
						setBlockHandler.setBlock(pos, blockState);
					}
				}
				if (setBlockHandler.failed) {
					throw new CommandException("text.cutelessmod.clientcommands.randomize.outOfWorld");
				}
				setBlockHandler.sendAffectedBlocks = true;
			}
		}
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		if (args.length > 0 && args.length <= 3) {
			return getTabCompletionCoordinate(args, 0, pos);
		} else if (args.length > 3 && args.length <= 6) {
			return getTabCompletionCoordinate(args, 3, pos);
		} else if (args.length >= 8) {
			return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
		} else {
			return Collections.emptyList();
		}
	}
}
