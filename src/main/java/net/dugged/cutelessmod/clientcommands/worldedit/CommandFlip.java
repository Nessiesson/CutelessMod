package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandFlip extends CommandBase {

	@Override
	public String getName() {
		return "flip";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.flip.usage").getUnformattedText();
	}

	private void flipSelection(World world) {
		HandlerSetBlock handler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world);
		handler.isWorldEditHandler = true;
		Map<BlockPos, IBlockState> blockList = new HashMap<>();
		for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(WorldEdit.getMinPos(), WorldEdit.getMaxPos())) {
			blockList.put(pos, world.getBlockState(pos));
		}
		EnumFacing direction = WorldEdit.getLookingDirection();
		for (int x = 0; x <= WorldEdit.widthX(); x++) {
			for (int y = 0; y <= WorldEdit.widthY(); y++) {
				for (int z = 0; z <= WorldEdit.widthZ(); z++) {
					if (direction.getAxis() == EnumFacing.Axis.Y) {
						handler.setBlock(WorldEdit.getMinPos().add(x, y, z), blockList.get(WorldEdit.getMinPos().add(x, WorldEdit.widthY() - y - 1, z)));
					} else if (direction.getAxis() == EnumFacing.Axis.Z) {
						handler.setBlock(WorldEdit.getMinPos().add(x, y, z), blockList.get(WorldEdit.getMinPos().add(x, y, WorldEdit.widthZ() - z - 1)));
					} else {
						handler.setBlock(WorldEdit.getMinPos().add(x, y, z), blockList.get(WorldEdit.getMinPos().add(WorldEdit.widthX() - x - 1, y, z)));
					}
				}
			}
		}
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			if (WorldEdit.hasSelection()) {
				World world = sender.getEntityWorld();
				Thread t = new Thread(() -> flipSelection(world));
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
