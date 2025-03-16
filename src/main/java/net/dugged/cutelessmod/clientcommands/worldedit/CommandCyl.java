package net.dugged.cutelessmod.clientcommands.worldedit;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;

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

public class CommandCyl extends ClientCommand {

	@Override
	public String getName() {
		return "cyl";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.cyl.usage").getUnformattedText();
	}

	private void generateCyl(World world, WorldEditSelection selection, BlockPos center,
		IBlockState blockState, double radius, int height) {
		if (height > world.getHeight() - center.getY()) {
			height = world.getHeight() - center.getY();
		}
		Map<AxisAlignedBB, IBlockState> regionMap = new LinkedHashMap<>();
		for (double x = 0; x <= radius; x++) {
			for (double z = 0; z <= radius; z++) {
				if (Thread.interrupted()) {
					return;
				}
				if (WorldEdit.checkCircle(x, z, radius)) {
					if (!WorldEdit.checkCircle(x + 1, z, radius) || !WorldEdit.checkCircle(x, z + 1,
						radius)) {
						BlockPos pos1 = new BlockPos(center.getX() + (int) x, center.getY(),
							center.getZ() - (int) z);
						BlockPos pos2 = new BlockPos(center.getX() + (int) x,
							center.getY() + height - 1, center.getZ() + (int) z);
						AxisAlignedBB box1 = new AxisAlignedBB(pos1, pos2.add(1, 1, 1));
						regionMap.put(box1, blockState);
						BlockPos pos3 = new BlockPos(center.getX() - (int) x, center.getY(),
							center.getZ() - (int) z);
						BlockPos pos4 = new BlockPos(center.getX() - (int) x,
							center.getY() + height - 1, center.getZ() + (int) z);
						AxisAlignedBB box2 = new AxisAlignedBB(pos3, pos4.add(1, 1, 1));
						regionMap.put(box2, blockState);
					}
				}
			}
		}
		TaskFill task = new TaskFill(regionMap, world);
		TaskManager.getInstance().addTask(task);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (args.length >= 3 && args.length <= 4) {
			if (WorldEdit.hasCurrentSelection()) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				if (selection.isOneByOne()) {
					World world = sender.getEntityWorld();
					Block block = getBlockByText(sender, args[0]);
					IBlockState blockState = convertArgToBlockState(block, args[1]);
					double radius = parseInt(args[2]) + 0.5;
					int height;
					if (args.length > 3) {
						height = Math.max(1, parseInt(args[3]));
					} else {
						height = 1;
					}
					Thread t = new Thread(
						() -> generateCyl(world, selection, selection.getPos(A), blockState, radius,
							height));
					t.start();
					TaskManager.getInstance().threads.add(t);
				} else {
					WorldEdit.sendMessage(new TextComponentTranslation(
						"text.cutelessmod.clientcommands.worldEdit.noOneByOneSelected"));
				}
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation(
					"text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
			}
		} else {
			WorldEdit.sendMessage(getUsage(sender));
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
