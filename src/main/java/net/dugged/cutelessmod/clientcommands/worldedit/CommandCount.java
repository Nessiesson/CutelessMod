package net.dugged.cutelessmod.clientcommands.worldedit;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandCount extends CommandBase {
	@Override
	public String getName() {
		return "count";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.count.usage").getUnformattedText();
	}

	private void countBlock(World world, IBlockState blockState, boolean compareStates) {
		int count = 0;
		for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(WorldEdit.posA, WorldEdit.posB)) {
			if ((compareStates && world.getBlockState(pos) == blockState) || (!compareStates && world.getBlockState(pos).getBlock() == blockState.getBlock())) {
				count++;
			}
		}
		WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.count.response", count));
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 1 || args.length == 2) {
			if (WorldEdit.hasSelection()) {
				Block block = getBlockByText(sender, args[0]);
				IBlockState blockState;
				boolean compareStates;
				if (args.length == 2) {
					blockState = convertArgToBlockState(block, args[1]);
					compareStates = true;
				} else {
					blockState = block.getDefaultState();
					compareStates = false;
				}
				World world = sender.getEntityWorld();
				Thread t = new Thread(() -> countBlock(world, blockState, compareStates));
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
