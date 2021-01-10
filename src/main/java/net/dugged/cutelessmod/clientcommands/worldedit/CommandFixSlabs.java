package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
import net.dugged.cutelessmod.clientcommands.HandlerUndo;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class CommandFixSlabs extends ClientCommand {
	@Override
	public String getName() {
		return "fixslabs";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.fixslabs.usage").getUnformattedText();
	}

	private void fixSlabs(World world) {
		int count = 0;
		HandlerSetBlock setBlockHandler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world);
		List<BlockPos> undoBlockPositions = new ArrayList<>();
		HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, world);
		undoHandler.setHandler(setBlockHandler);
		undoHandler.running = false;
		for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(WorldEdit.posA, WorldEdit.posB)) {
			IBlockState blockState = world.getBlockState(pos);
			if (blockState.getBlock() instanceof BlockDoubleStoneSlab && blockState.getProperties().containsKey(BlockDoubleStoneSlab.VARIANT)) {
				switch (blockState.getValue(BlockDoubleStoneSlab.VARIANT)) {
					case SAND:
						undoBlockPositions.add(pos);
						setBlockHandler.setBlock(pos, Blocks.SANDSTONE.getDefaultState());
						count++;
						break;
					case WOOD:
						undoBlockPositions.add(pos);
						setBlockHandler.setBlock(pos, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.OAK));
						count++;
						break;
					case COBBLESTONE:
						undoBlockPositions.add(pos);
						setBlockHandler.setBlock(pos, Blocks.COBBLESTONE.getDefaultState());
						count++;
						break;
					case BRICK:
						undoBlockPositions.add(pos);
						setBlockHandler.setBlock(pos, Blocks.BRICK_BLOCK.getDefaultState());
						count++;
						break;
					case SMOOTHBRICK:
						undoBlockPositions.add(pos);
						setBlockHandler.setBlock(pos, Blocks.STONEBRICK.getDefaultState());
						count++;
						break;
					case NETHERBRICK:
						undoBlockPositions.add(pos);
						setBlockHandler.setBlock(pos, Blocks.NETHER_BRICK.getDefaultState());
						count++;
						break;
					case QUARTZ:
						undoBlockPositions.add(pos);
						setBlockHandler.setBlock(pos, Blocks.QUARTZ_BLOCK.getDefaultState());
						count++;
						break;
					default:
						break;
				}
			} else if (blockState.getBlock() instanceof BlockDoubleStoneSlabNew && blockState.getProperties().containsKey(BlockDoubleStoneSlabNew.VARIANT)) {
				switch (blockState.getValue(BlockDoubleStoneSlabNew.VARIANT)) {
					case RED_SANDSTONE:
						undoBlockPositions.add(pos);
						setBlockHandler.setBlock(pos, Blocks.RED_SANDSTONE.getDefaultState());
						count++;
						break;
					default:
						break;
				}
			} else if (blockState.getBlock() instanceof BlockDoubleWoodSlab && blockState.getProperties().containsKey(BlockDoubleWoodSlab.VARIANT)) {
				switch (blockState.getValue(BlockDoubleWoodSlab.VARIANT)) {
					case OAK:
						undoBlockPositions.add(pos);
						setBlockHandler.setBlock(pos, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.OAK));
						count++;
						break;
					case SPRUCE:
						undoBlockPositions.add(pos);
						setBlockHandler.setBlock(pos, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE));
						count++;
						break;
					case BIRCH:
						undoBlockPositions.add(pos);
						setBlockHandler.setBlock(pos, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.BIRCH));
						count++;
						break;
					case JUNGLE:
						undoBlockPositions.add(pos);
						setBlockHandler.setBlock(pos, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE));
						count++;
						break;
					case ACACIA:
						undoBlockPositions.add(pos);
						setBlockHandler.setBlock(pos, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.ACACIA));
						count++;
						break;
					case DARK_OAK:
						undoBlockPositions.add(pos);
						setBlockHandler.setBlock(pos, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.DARK_OAK));
						count++;
						break;
					default:
						break;
				}
			} else if (blockState.getBlock() instanceof BlockPurpurSlab && ((BlockPurpurSlab) blockState.getBlock()).isDouble()) {
				undoBlockPositions.add(pos);
				setBlockHandler.setBlock(pos, Blocks.PURPUR_BLOCK.getDefaultState());
				count++;
			} else if (blockState.getBlock() instanceof BlockStoneSlab && blockState.getProperties().containsKey(BlockStoneSlab.VARIANT) && blockState.getValue(BlockStoneSlab.VARIANT) == BlockStoneSlab.EnumType.WOOD) {
				undoBlockPositions.add(pos);
				setBlockHandler.setBlock(pos, Blocks.WOODEN_SLAB.getDefaultState().withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.OAK).withProperty(BlockSlab.HALF, blockState.getValue(BlockStoneSlab.HALF)));
				count++;
			}
		}
		undoHandler.saveBlocks(undoBlockPositions);
		undoHandler.running = true;
		WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.fixslabs.response", count));
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			if (WorldEdit.hasSelection()) {
				World world = sender.getEntityWorld();
				Thread t = new Thread(() -> fixSlabs(world));
				t.start();
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
			}
		} else {
			WorldEdit.sendMessage(getUsage(sender));
		}
	}
}
