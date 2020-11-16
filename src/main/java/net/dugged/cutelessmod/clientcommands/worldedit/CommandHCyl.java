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
			if (WorldEdit.hasSelection() && WorldEdit.widthX() == 1 && WorldEdit.widthY() == 1 && WorldEdit.widthZ() == 1) {
				World world = sender.getEntityWorld();
				HandlerFill handler = (HandlerFill) ClientCommandHandler.instance.createHandler(HandlerFill.class, world);
				handler.isWorldEditHandler = true;
				Block block = CommandBase.getBlockByText(sender, args[0]);
				IBlockState blockstate = convertArgToBlockState(block, args[1]);
				int radius = parseInt(args[2]);
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
				int x = 0;
				int z = radius;
				int d = 3 - 2 * radius;
				BlockPos pos = new BlockPos(WorldEdit.posA.getX() + radius, WorldEdit.posA.getY(), WorldEdit.posA.getZ());
				handler.fill(pos, pos.up(height - 1), blockstate);
				pos = new BlockPos(WorldEdit.posA.getX() - radius, WorldEdit.posA.getY(), WorldEdit.posA.getZ());
				handler.fill(pos, pos.up(height - 1), blockstate);
				pos = new BlockPos(WorldEdit.posA.getX(), WorldEdit.posA.getY(), WorldEdit.posA.getZ() + radius);
				handler.fill(pos, pos.up(height - 1), blockstate);
				pos = new BlockPos(WorldEdit.posA.getX(), WorldEdit.posA.getY(), WorldEdit.posA.getZ() - radius);
				handler.fill(pos, pos.up(height - 1), blockstate);
				while (x <= z) {
					if (d <= 0) {
						d = d + (4 * x + 6);
					} else {
						d = d + 4 * (x - z) + 10;
						z--;
					}
					x++;
					pos = new BlockPos(WorldEdit.posA.getX() + x, WorldEdit.posA.getY(), WorldEdit.posA.getZ() + z);
					handler.fill(pos, pos.up(height - 1), blockstate);
					pos = new BlockPos(WorldEdit.posA.getX() - x, WorldEdit.posA.getY(), WorldEdit.posA.getZ() + z);
					handler.fill(pos, pos.up(height - 1), blockstate);
					pos = new BlockPos(WorldEdit.posA.getX() + x, WorldEdit.posA.getY(), WorldEdit.posA.getZ() - z);
					handler.fill(pos, pos.up(height - 1), blockstate);
					pos = new BlockPos(WorldEdit.posA.getX() - x, WorldEdit.posA.getY(), WorldEdit.posA.getZ() - z);
					handler.fill(pos, pos.up(height - 1), blockstate);
					pos = new BlockPos(WorldEdit.posA.getX() + z, WorldEdit.posA.getY(), WorldEdit.posA.getZ() + x);
					handler.fill(pos, pos.up(height - 1), blockstate);
					pos = new BlockPos(WorldEdit.posA.getX() - z, WorldEdit.posA.getY(), WorldEdit.posA.getZ() + x);
					handler.fill(pos, pos.up(height - 1), blockstate);
					pos = new BlockPos(WorldEdit.posA.getX() + z, WorldEdit.posA.getY(), WorldEdit.posA.getZ() - x);
					handler.fill(pos, pos.up(height - 1), blockstate);
					pos = new BlockPos(WorldEdit.posA.getX() - z, WorldEdit.posA.getY(), WorldEdit.posA.getZ() - x);
					handler.fill(pos, pos.up(height - 1), blockstate);
				}
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noOneByOneSelected"));
			}
		} else {
			WorldEdit.sendMessage(getUsage(sender));
		}
	}
}
