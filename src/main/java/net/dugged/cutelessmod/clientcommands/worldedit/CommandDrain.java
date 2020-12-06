package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
import net.minecraft.block.BlockLiquid;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandDrain extends CommandBase {
	@Override
	public String getName() {
		return "drain";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.drain.usage").getUnformattedText();
	}

	private void drainBody(World world, BlockPos startPos, int radius) {
		HandlerSetBlock handler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world);
		handler.isWorldEditHandler = true;
		handler.autoCancel = false;
		Map<ChunkPos, BlockPos> chunkMap = new HashMap<>();
		List<ChunkPos> chunkList = new ArrayList<>();
		List<BlockPos> blockList = new ArrayList<>();
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
				pos1 = pos.up();
				if (world.getBlockState(pos1).getBlock() instanceof BlockLiquid && WorldEdit.checkCircle(pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
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
				pos1 = pos.down();
				if (world.getBlockState(pos1).getBlock() instanceof BlockLiquid && WorldEdit.checkCircle(pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
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
				if (world.getBlockState(pos1).getBlock() instanceof BlockLiquid && WorldEdit.checkCircle(pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
					ChunkPos chunkPos1 = world.getChunk(pos1).getPos();
					if (chunkPos1.equals(chunkPos)) {
						if (!checkedBlocks.contains(pos1)) {
							blocksToCheck.add(pos1);
							checkedBlocks.add(pos1);
						}
					} else {
						handler.setBlock(pos1, Blocks.AIR.getDefaultState());
						if (!chunkMap.containsKey(chunkPos1)) {
							chunkList.add(chunkPos1);
							chunkMap.put(chunkPos1, pos1);
						}
					}
				}
				pos1 = pos.east();
				if (world.getBlockState(pos1).getBlock() instanceof BlockLiquid && WorldEdit.checkCircle(pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
					ChunkPos chunkPos1 = world.getChunk(pos1).getPos();
					if (chunkPos1.equals(chunkPos)) {
						if (!checkedBlocks.contains(pos1)) {
							blocksToCheck.add(pos1);
							checkedBlocks.add(pos1);
						}
					} else {
						handler.setBlock(pos1, Blocks.AIR.getDefaultState());
						if (!chunkMap.containsKey(chunkPos1)) {
							chunkList.add(chunkPos1);
							chunkMap.put(chunkPos1, pos1);
						}
					}
				}
				pos1 = pos.south();
				if (world.getBlockState(pos1).getBlock() instanceof BlockLiquid && WorldEdit.checkCircle(pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
					ChunkPos chunkPos1 = world.getChunk(pos1).getPos();
					if (chunkPos1.equals(chunkPos)) {
						if (!checkedBlocks.contains(pos1)) {
							blocksToCheck.add(pos1);
							checkedBlocks.add(pos1);
						}
					} else {
						handler.setBlock(pos1, Blocks.AIR.getDefaultState());
						if (!chunkMap.containsKey(chunkPos1)) {
							chunkList.add(chunkPos1);
							chunkMap.put(chunkPos1, pos1);
						}
					}
				}
				pos1 = pos.west();
				if (world.getBlockState(pos1).getBlock() instanceof BlockLiquid && WorldEdit.checkCircle(pos1.getX() - startPos.getX(), pos1.getZ() - startPos.getZ(), radius)) {
					ChunkPos chunkPos1 = world.getChunk(pos1).getPos();
					if (chunkPos1.equals(chunkPos)) {
						if (!checkedBlocks.contains(pos1)) {
							blocksToCheck.add(pos1);
							checkedBlocks.add(pos1);
						}
					} else {
						handler.setBlock(pos1, Blocks.AIR.getDefaultState());
						if (!chunkMap.containsKey(chunkPos1)) {
							chunkList.add(chunkPos1);
							chunkMap.put(chunkPos1, pos1);
						}
					}
				}
				blocksToCheck.remove(0);
				handler.setBlock(pos, Blocks.AIR.getDefaultState());
				blockList.add(pos);
			}
			chunkList.remove(0);
		}
		handler.autoCancel = true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0 || args.length == 1) {
			World world = sender.getEntityWorld();
			BlockPos pos = WorldEdit.playerPos();
			if (world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
				int radius;
				if (args.length == 1) {
					radius = parseInt(args[0]);
				} else {
					radius = 100;
				}
				Thread t = new Thread(() -> drainBody(world, pos, radius));
				t.start();
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.drain.notInWater"));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.drain.usage"));
		}
	}
}
