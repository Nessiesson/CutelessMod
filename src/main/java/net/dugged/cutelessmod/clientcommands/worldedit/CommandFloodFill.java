package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public class CommandFloodFill extends ClientCommand {
	@Override
	public String getName() {
		return "floodfill";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.floodfill.usage").getUnformattedText();
	}

	private void floodFill(World world, IBlockState blockState, BlockPos startPos, int radius) {
		HandlerSetBlock handler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world);
		handler.isWorldEditHandler = true;
		handler.autoCancel = false;
		List<ChunkPos> chunkList = new ArrayList<>();
		Map<ChunkPos, BlockPos> chunkMap = new HashMap<>();
		chunkList.add(world.getChunk(startPos).getPos());
		chunkMap.put(world.getChunk(startPos).getPos(), startPos);
		BlockPos pos1;
		while (chunkList.size() > 0) {
			ChunkPos chunkPos = chunkList.get(0);
			BlockPos chunkStartPos = chunkMap.get(chunkPos);
			List<BlockPos> checkedBlocks = new ArrayList<>();
			List<BlockPos> blocksToCheck = new ArrayList<>();
			blocksToCheck.add(chunkStartPos);
			checkedBlocks.add(chunkStartPos);
			while (blocksToCheck.size() > 0) {
				BlockPos pos = blocksToCheck.get(0);
				if (pos.up().getY() <= startPos.getY()) {
					pos1 = pos.up();
					if (world.getBlockState(pos1).getBlock() instanceof BlockAir && WorldEdit.checkCircle(pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
						ChunkPos chunkPos1 = world.getChunk(pos1).getPos();
						if (chunkPos1.equals(chunkPos)) {
							if (!checkedBlocks.contains(pos1)) {
								blocksToCheck.add(pos1);
								checkedBlocks.add(pos1);
							}
						} else {
							if (!chunkMap.containsKey(chunkPos1)) {
								chunkList.add(chunkPos1);
								chunkMap.put(chunkPos1, pos1);
							}
						}
					}
				}
				pos1 = pos.down();
				if (world.getBlockState(pos1).getBlock() instanceof BlockAir && WorldEdit.checkCircle(pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
					ChunkPos chunkPos1 = world.getChunk(pos1).getPos();
					if (chunkPos1.equals(chunkPos)) {
						if (!checkedBlocks.contains(pos1)) {
							blocksToCheck.add(pos1);
							checkedBlocks.add(pos1);
						}
					} else {
						if (!chunkMap.containsKey(chunkPos1)) {
							chunkList.add(chunkPos1);
							chunkMap.put(chunkPos1, pos1);
						}
					}
				}
				pos1 = pos.north();
				if (world.getBlockState(pos1).getBlock() instanceof BlockAir && WorldEdit.checkCircle(pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
					ChunkPos chunkPos1 = world.getChunk(pos1).getPos();
					if (chunkPos1.equals(chunkPos)) {
						if (!checkedBlocks.contains(pos1)) {
							blocksToCheck.add(pos1);
							checkedBlocks.add(pos1);
						}
					} else {
						if (!chunkMap.containsKey(chunkPos1)) {
							chunkList.add(chunkPos1);
							chunkMap.put(chunkPos1, pos1);
						}
					}
				}
				pos1 = pos.east();
				if (world.getBlockState(pos1).getBlock() instanceof BlockAir && WorldEdit.checkCircle(pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
					ChunkPos chunkPos1 = world.getChunk(pos1).getPos();
					if (chunkPos1.equals(chunkPos)) {
						if (!checkedBlocks.contains(pos1)) {
							blocksToCheck.add(pos1);
							checkedBlocks.add(pos1);
						}
					} else {
						if (!chunkMap.containsKey(chunkPos1)) {
							chunkList.add(chunkPos1);
							chunkMap.put(chunkPos1, pos1);
						}
					}
				}
				pos1 = pos.south();
				if (world.getBlockState(pos1).getBlock() instanceof BlockAir && WorldEdit.checkCircle(pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
					ChunkPos chunkPos1 = world.getChunk(pos1).getPos();
					if (chunkPos1.equals(chunkPos)) {
						if (!checkedBlocks.contains(pos1)) {
							blocksToCheck.add(pos1);
							checkedBlocks.add(pos1);
						}
					} else {
						if (!chunkMap.containsKey(chunkPos1)) {
							chunkList.add(chunkPos1);
							chunkMap.put(chunkPos1, pos1);
						}
					}
				}
				pos1 = pos.west();
				if (world.getBlockState(pos1).getBlock() instanceof BlockAir && WorldEdit.checkCircle(pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
					ChunkPos chunkPos1 = world.getChunk(pos1).getPos();
					if (chunkPos1.equals(chunkPos)) {
						if (!checkedBlocks.contains(pos1)) {
							blocksToCheck.add(pos1);
							checkedBlocks.add(pos1);
						}
					} else {
						if (!chunkMap.containsKey(chunkPos1)) {
							chunkList.add(chunkPos1);
							chunkMap.put(chunkPos1, pos1);
						}
					}
				}
				handler.setBlock(pos, blockState);
				blocksToCheck.remove(0);
			}
			chunkList.remove(0);
		}
		handler.autoCancel = true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length >= 1 && args.length <= 3) {
			World world = sender.getEntityWorld();
			BlockPos pos = WorldEdit.playerPos();
			if (world.getBlockState(pos).getBlock() instanceof BlockAir) {
				Block block = getBlockByText(sender, args[0]);
				IBlockState blockState;
				if (args.length >= 2) {
					blockState = convertArgToBlockState(block, args[1]);
				} else {
					blockState = block.getDefaultState();
				}
				int radius;
				if (args.length == 3) {
					radius = parseInt(args[2]);
				} else {
					radius = 100;
				}
				Thread t = new Thread(() -> floodFill(world, blockState, pos, radius));
				t.start();
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.floodfill.noSpaceToFlood"));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.floodfill.usage"));
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
