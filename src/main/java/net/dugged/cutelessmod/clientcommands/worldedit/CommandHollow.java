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

public class CommandHollow extends ClientCommand {

	@Override
	public String getName() {
		return "hollow";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.hollow.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (WorldEdit.hasCurrentSelection()) {
			WorldEditSelection selection = WorldEdit.getCurrentSelection();
			if (args.length > 0 && args.length <= 3) {
				Block block = getBlockByText(sender, args[0]);
				IBlockState blockState = block.getDefaultState();
				if (args.length >= 2) {
					blockState = convertArgToBlockState(block, args[1]);
				}
				int thickness = 0;
				if (args.length >= 3) {
					thickness = parseInt(args[2]) - 1;
					if (thickness <= 0) {
						thickness = 0;
					}
				}
				BlockPos posMin = selection.minPos();
				BlockPos posMax = selection.maxPos();
				int thicknessX = Math.min(thickness, selection.widthX() - 1);
				int thicknessY = Math.min(thickness, selection.widthY() - 1);
				int thicknessZ = Math.min(thickness, selection.widthZ() - 1);

				Map<AxisAlignedBB, IBlockState> regionMap = new LinkedHashMap<>();
				// Front face (posMin.z side)
				{
					BlockPos corner2 = new BlockPos(posMax.getX(), posMax.getY(),
						posMin.getZ() + thicknessZ);
					regionMap.put(new AxisAlignedBB(posMin, corner2.add(1, 1, 1)), blockState);
				}
				// Left face (posMin.x side)
				{
					BlockPos corner2 = new BlockPos(posMin.getX() + thicknessX, posMax.getY(),
						posMax.getZ());
					regionMap.put(new AxisAlignedBB(posMin, corner2.add(1, 1, 1)), blockState);
				}
				// Bottom face (posMin.y side)
				{
					BlockPos corner2 = new BlockPos(posMax.getX(), posMin.getY() + thicknessY,
						posMax.getZ());
					regionMap.put(new AxisAlignedBB(posMin, corner2.add(1, 1, 1)), blockState);
				}
				// Back face (posMax.z side)
				{
					BlockPos corner1 = new BlockPos(posMin.getX(), posMin.getY(),
						posMax.getZ() - thicknessZ);
					regionMap.put(new AxisAlignedBB(corner1, posMax.add(1, 1, 1)), blockState);
				}
				// Right face (posMax.x side)
				{
					BlockPos corner1 = new BlockPos(posMax.getX() - thicknessX, posMin.getY(),
						posMin.getZ());
					regionMap.put(new AxisAlignedBB(corner1, posMax.add(1, 1, 1)), blockState);
				}
				// Top face (posMax.y side)
				{
					BlockPos corner1 = new BlockPos(posMin.getX(), posMax.getY() - thicknessY,
						posMin.getZ());
					regionMap.put(new AxisAlignedBB(corner1, posMax.add(1, 1, 1)), blockState);
				}
				TaskFill task = new TaskFill(regionMap, sender.getEntityWorld());
				TaskManager.getInstance().addTask(task);
			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation(
				"text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
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