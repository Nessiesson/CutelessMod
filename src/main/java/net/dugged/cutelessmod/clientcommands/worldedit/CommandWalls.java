package net.dugged.cutelessmod.clientcommands.worldedit;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.TaskFill;
import net.dugged.cutelessmod.clientcommands.TaskManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandWalls extends ClientCommand {

	@Override
	public String getName() {
		return "walls";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.walls.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (WorldEdit.hasCurrentSelection()) {
			if (args.length > 0 && args.length <= 3) {
				Block block = getBlockByText(sender, args[0]);
				IBlockState blockState = block.getDefaultState();
				int thickness = 0;
				if (args.length >= 2) {
					blockState = convertArgToBlockState(block, args[1]);
				}
				if (args.length >= 3) {
					thickness = parseInt(args[2]) - 1;
					if (thickness <= 0) {
						thickness = 0;
					}
				}
				World world = sender.getEntityWorld();
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				BlockPos posMin = selection.minPos();
				BlockPos posMax = selection.maxPos();
				int wallThicknessZ = Math.min(thickness, selection.widthZ() - 1);
				int wallThicknessX = Math.min(thickness, selection.widthX() - 1);
				Map<AxisAlignedBB, IBlockState> regionMap = new LinkedHashMap<>();
				regionMap.put(new AxisAlignedBB(posMin,
					new BlockPos(posMax.getX(), posMax.getY(), posMin.getZ() + wallThicknessZ).add(
						1, 1, 1)), blockState);
				regionMap.put(new AxisAlignedBB(posMin,
					new BlockPos(posMin.getX() + wallThicknessX, posMax.getY(), posMax.getZ()).add(
						1, 1, 1)), blockState);
				regionMap.put(new AxisAlignedBB(
					new BlockPos(posMin.getX(), posMin.getY(), posMax.getZ() - wallThicknessZ),
					posMax.add(1, 1, 1)), blockState);
				regionMap.put(new AxisAlignedBB(
					new BlockPos(posMax.getX() - wallThicknessX, posMin.getY(), posMin.getZ()),
					posMax.add(1, 1, 1)), blockState);
				TaskFill task = new TaskFill(regionMap, world);
				TaskManager.getInstance().addTask(task);
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
			return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
		} else {
			return Collections.emptyList();
		}
	}
}