package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerFill;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class CommandUpscale extends ClientCommand {
	@Override
	public String getName() {
		return "upscale";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.upscale.usage").getUnformattedText();
	}

	private void upscaleSelection(World world, int factor) {
		Map<BlockPos, IBlockState> blockList = new HashMap<>();
		if (factor <= 0) {
			factor = 1;
		}
		for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(WorldEdit.minPos(), WorldEdit.maxPos())) {
			blockList.put(pos, world.getBlockState(pos));
		}
		HandlerFill handler = (HandlerFill) ClientCommandHandler.instance.createHandler(HandlerFill.class, world);
		handler.isWorldEditHandler = true;
		for (int x = 0; x < WorldEdit.widthX(); x++) {
			for (int y = 0; y < WorldEdit.widthY(); y++) {
				for (int z = 0; z < WorldEdit.widthZ(); z++) {
					IBlockState blockState = world.getBlockState(new BlockPos(WorldEdit.minPos().getX() + x, WorldEdit.minPos().getY() + y, WorldEdit.minPos().getZ() + z));
					BlockPos minPos = new BlockPos(WorldEdit.minPos().getX() + (x * factor), WorldEdit.minPos().getY() + (y * factor), WorldEdit.minPos().getZ() + (z * factor));
					BlockPos maxPos = new BlockPos(minPos.getX() + (factor - 1), minPos.getY() + (factor - 1), minPos.getZ() + (factor - 1));
					boolean skip = true;
					for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(minPos, maxPos)) {
						if (world.getBlockState(pos) != blockState) {
							skip = false;
							break;
						}
					}
					if (!skip) {
						handler.fill(minPos, maxPos, blockState);
					}
				}
			}
		}
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (WorldEdit.hasSelection()) {
			if (args.length == 1) {
				int factor = parseInt(args[0]);
				World world = sender.getEntityWorld();
				Thread t = new Thread(() -> upscaleSelection(world, factor));
				t.start();
			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}
}
