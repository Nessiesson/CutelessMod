package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
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

public class CommandPolygon extends ClientCommand {

	@Override
	public String getName() {
		return "polygon";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.polygon.usage").getUnformattedText();
	}

	private void generatePolygon(World world, IBlockState blockstate, int radius, int points, boolean halfStep) {
		HandlerSetBlock handler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world);
		handler.isWorldEditHandler = true;
		handler.autoCancel = false;
		double rotation = Math.PI / points;
		if (halfStep) {
			rotation = Math.PI / (points * 2);
		}
		for (int i = 1; i < points + 1; i++) {
			int x1 = (int) (radius * Math.cos(2 * (-(Math.PI + rotation) + (Math.PI / points) * i)) + WorldEdit.posA.getX());
			int z1 = (int) (radius * Math.sin(2 * (-(Math.PI + rotation) + (Math.PI / points) * i)) + WorldEdit.posA.getZ());
			int x2 = (int) (radius * Math.cos(2 * (-(Math.PI + rotation) + (Math.PI / points) * (i + 1))) + WorldEdit.posA.getX());
			int z2 = (int) (radius * Math.sin(2 * (-(Math.PI + rotation) + (Math.PI / points) * (i + 1))) + WorldEdit.posA.getZ());
			int dx = Math.abs(x2 - x1);
			int dz = Math.abs(z2 - z1);
			int err, sx, sz;
			if (x1 > x2) {
				sx = -1;
			} else {
				sx = 1;
			}
			if (z1 > z2) {
				sz = -1;
			} else {
				sz = 1;
			}
			if (dx > dz) {
				err = dx / 2;
				while (x1 != x2) {
					handler.setBlock(new BlockPos(x1, WorldEdit.posA.getY(), z1), blockstate);
					err -= dz;
					if (err < 0) {
						z1 += sz;
						err += dx;
					}
					x1 += sx;
				}
			} else {
				err = dz / 2;
				while (z1 != z2) {
					handler.setBlock(new BlockPos(x1, WorldEdit.posA.getY(), z1), blockstate);
					err -= dx;
					if (err < 0) {
						x1 += sx;
						err += dz;
					}
					z1 += sz;
				}
			}
		}
		handler.autoCancel = true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length >= 4 && args.length <= 5) {
			if (WorldEdit.hasSelection() && WorldEdit.isOneByOne()) {
				Block block = getBlockByText(sender, args[0]);
				IBlockState blockstate = convertArgToBlockState(block, args[1]);
				World world = sender.getEntityWorld();
				int radius = parseInt(args[2]);
				int points = parseInt(args[3]);
				boolean halfStep;
				if (args.length == 5) {
					halfStep = parseBoolean(args[4]);
				} else {
					halfStep = false;
				}

				Thread t = new Thread(() -> generatePolygon(world, blockstate, radius, points, halfStep));
				t.start();
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
		} else if (args.length == 5) {
			return getListOfStringsMatchingLastWord(args, "true", "false");
		} else {
			return Collections.emptyList();
		}
	}
}
