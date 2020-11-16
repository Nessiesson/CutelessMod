package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerFill;
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

public class CommandHCyl extends CommandBase {
	@Override
	public String getName() {
		return "hcyl";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.hcyl.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length >= 3 && args.length <= 4) {
			if (WorldEdit.hasSelection() && WorldEdit.isOneByOne()) {
				World world = sender.getEntityWorld();
				HandlerFill handler = (HandlerFill) ClientCommandHandler.instance.createHandler(HandlerFill.class, world);
				handler.isWorldEditHandler = true;
				Block block = CommandBase.getBlockByText(sender, args[0]);
				IBlockState blockstate = convertArgToBlockState(block, args[1]);
				double radius = parseInt(args[2]) + 0.5;
				int height = 1;
				if (args.length > 3) {
					height = parseInt(args[3]);
					if (height <= 0) {
						height = 1;
					}
				}
				if (height > world.getHeight() - WorldEdit.posA.getY()) {
					height = world.getHeight() - WorldEdit.posA.getY();
				}
				for (double x = 0; x <= radius; x++) {
					for (double z = 0; z <= radius; z++) {
						if (WorldEdit.checkCircle(x, z, radius)) {
							if (!WorldEdit.checkCircle(x + 1, z, radius) || !WorldEdit.checkCircle(x, z + 1, radius)) {
								handler.fill(new BlockPos(WorldEdit.posA.getX() + x, WorldEdit.posA.getY(), WorldEdit.posA.getZ() + z), new BlockPos(WorldEdit.posA.getX() + x, WorldEdit.posA.getY() + height - 1, WorldEdit.posA.getZ() + z), blockstate);
								handler.fill(new BlockPos(WorldEdit.posA.getX() + x, WorldEdit.posA.getY(), WorldEdit.posA.getZ() - z), new BlockPos(WorldEdit.posA.getX() + x, WorldEdit.posA.getY() + height - 1, WorldEdit.posA.getZ() - z), blockstate);
								handler.fill(new BlockPos(WorldEdit.posA.getX() - x, WorldEdit.posA.getY(), WorldEdit.posA.getZ() + z), new BlockPos(WorldEdit.posA.getX() - x, WorldEdit.posA.getY() + height - 1, WorldEdit.posA.getZ() + z), blockstate);
								handler.fill(new BlockPos(WorldEdit.posA.getX() - x, WorldEdit.posA.getY(), WorldEdit.posA.getZ() - z), new BlockPos(WorldEdit.posA.getX() - x, WorldEdit.posA.getY() + height - 1, WorldEdit.posA.getZ() - z), blockstate);
							}
						}
					}
				}
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noOneByOneSelected"));
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
