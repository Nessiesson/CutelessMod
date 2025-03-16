package net.dugged.cutelessmod.clientcommands.worldedit;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.TaskManager;
import net.dugged.cutelessmod.clientcommands.TaskReplaceItem;
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
		Map<BlockPos, ItemStack> containerMap = new HashMap<>();
		for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(selection.getPos(A),
			selection.getPos(B))) {
			if (Thread.interrupted()) {
				return;
			}
			if (world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockDispenser
				|| world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockChest
				|| world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockHopper) {
				containerMap.put(pos, stack);
			}
		}
		TaskReplaceItem task = new TaskReplaceItem(containerMap, world);
		TaskManager.getInstance().addTask(task);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (WorldEdit.hasCurrentSelection()) {
			if (args.length == 2 || args.length == 3) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				Item item = getItemByText(sender, args[0]);
				int count = Math.min(item.getItemStackLimit(), Math.max(parseInt(args[1]), 1));
				int damage = (args.length == 3) ? parseInt(args[2]) : 0;
				ItemStack stack = new ItemStack(item, count, damage);
				World world = sender.getEntityWorld();
				Thread t = new Thread(() -> fillInventories(world, selection, stack));
				t.start();
				TaskManager.getInstance().threads.add(t);
			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation(
				"text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
		String[] args, @Nullable BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, Item.REGISTRY.getKeys());
		} else {
			return Collections.emptyList();
		}
	}
}