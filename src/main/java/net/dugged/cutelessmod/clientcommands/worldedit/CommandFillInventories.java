package net.dugged.cutelessmod.clientcommands.worldedit;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerReplaceItem;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandFillInventories extends ClientCommand {

	@Override
	public String getName() {
		return "fillinventories";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.fillinventories.usage").getUnformattedText();
	}

	private void fillInventories(World world, WorldEditSelection selection, ItemStack stack) {
		HandlerReplaceItem handler = (HandlerReplaceItem) ClientCommandHandler.instance.createHandler(
			HandlerReplaceItem.class, world, selection);
		for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(selection.getPos(A),
			selection.getPos(B))) {
			if (Thread.interrupted()) {
				return;
			}
			IBlockState blockState = world.getBlockState(pos);
			if (blockState.getBlock() instanceof BlockDispenser
				|| blockState.getBlock() instanceof BlockChest
				|| blockState.getBlock() instanceof BlockHopper) {
				handler.fillContainer(pos, stack);
			}
		}
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (WorldEdit.hasCurrentSelection()) {
			if (args.length == 2 || args.length == 3) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				Item item = getItemByText(sender, args[0]);
				int count = Math.min(item.getItemStackLimit(), Math.max(parseInt(args[1]), 1));
				int damage = 0;
				if (args.length == 3) {
					damage = parseInt(args[2]);
				}
				ItemStack stack = new ItemStack(item, count, damage);
				World world = sender.getEntityWorld();
				Thread t = new Thread(() -> fillInventories(world, selection, stack));
				t.start();
				ClientCommandHandler.instance.threads.add(t);
			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation(
				"text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[]
		args, @Nullable BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, Item.REGISTRY.getKeys());
		} else {
			return Collections.emptyList();
		}
	}
}
