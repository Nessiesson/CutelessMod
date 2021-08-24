package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

public class CommandCount extends ClientCommand {
	@Override
	public String getName() {
		return "count";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.count.usage").getUnformattedText();
	}

	private void countBlock(World world, WorldEditSelection selection, IBlockState blockState, boolean exclusive, boolean compareStates) {
		int count = 0;
		for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(selection.getPos(A), selection.getPos(B))) {
			if (exclusive) {
				if (!((compareStates && world.getBlockState(pos) == blockState) || (!compareStates && world.getBlockState(pos).getBlock() == blockState.getBlock()))) {
					count++;
				}
			} else {
				if ((compareStates && world.getBlockState(pos) == blockState) || (!compareStates && world.getBlockState(pos).getBlock() == blockState.getBlock())) {
					count++;
				}
			}
		}
		if (exclusive) {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.count.responseInclusive", count));
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.count.responseExclusive", count));
		}
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 2 || args.length == 3) {
			if (WorldEdit.hasCurrentSelection()) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				boolean exclusive = parseBoolean(args[0]);
				Block block = getBlockByText(sender, args[1]);
				IBlockState blockState;
				boolean compareStates;
				if (args.length == 3) {
					blockState = convertArgToBlockState(block, args[2]);
					compareStates = true;
				} else {
					blockState = block.getDefaultState();
					compareStates = false;
				}
				World world = sender.getEntityWorld();
				Thread t = new Thread(() -> countBlock(world, selection, blockState, exclusive, compareStates));
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
			return getListOfStringsMatchingLastWord(args, "true", "false");
		} else if (args.length == 2) {
			return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
		} else {
			return Collections.emptyList();
		}
	}
}
