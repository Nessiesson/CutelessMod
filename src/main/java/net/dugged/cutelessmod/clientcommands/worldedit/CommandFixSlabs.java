package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandFixSlabs extends CommandBase {
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
		HandlerSetBlock handler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world);
		handler.isWorldEditHandler = true;
		handler.autoCancel = false;
		for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(WorldEdit.posA, WorldEdit.posB)) {
			IBlockState blockState = world.getBlockState(pos);
			if (blockState.getBlock() instanceof BlockDoubleStoneSlab && blockState.getProperties().containsKey(BlockDoubleStoneSlab.VARIANT)) {
				switch (blockState.getValue(BlockDoubleStoneSlab.VARIANT)) {
					case SAND:
						handler.setBlock(pos, Blocks.SANDSTONE.getDefaultState());
						count++;
						break;
					case WOOD:
						handler.setBlock(pos, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.OAK));
						count++;
						break;
					case COBBLESTONE:
						handler.setBlock(pos, Blocks.COBBLESTONE.getDefaultState());
						count++;
						break;
					case BRICK:
						handler.setBlock(pos, Blocks.BRICK_BLOCK.getDefaultState());
						count++;
						break;
					case SMOOTHBRICK:
						handler.setBlock(pos, Blocks.STONEBRICK.getDefaultState());
						count++;
						break;
					case NETHERBRICK:
						handler.setBlock(pos, Blocks.NETHER_BRICK.getDefaultState());
						count++;
						break;
					case QUARTZ:
						handler.setBlock(pos, Blocks.QUARTZ_BLOCK.getDefaultState());
						count++;
						break;
					default:
						break;
				}
			} else if (blockState.getBlock() instanceof BlockDoubleStoneSlabNew && blockState.getProperties().containsKey(BlockDoubleStoneSlabNew.VARIANT)) {
				switch (blockState.getValue(BlockDoubleStoneSlabNew.VARIANT)) {
					case RED_SANDSTONE:
						handler.setBlock(pos, Blocks.RED_SANDSTONE.getDefaultState());
						count++;
						break;
					default:
						break;
				}
			} else if (blockState.getBlock() instanceof BlockDoubleWoodSlab && blockState.getProperties().containsKey(BlockDoubleWoodSlab.VARIANT)) {
				switch (blockState.getValue(BlockDoubleWoodSlab.VARIANT)) {
					case OAK:
						handler.setBlock(pos, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.OAK));
						count++;
						break;
					case SPRUCE:
						handler.setBlock(pos, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE));
						count++;
						break;
					case BIRCH:
						handler.setBlock(pos, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.BIRCH));
						count++;
						break;
					case JUNGLE:
						handler.setBlock(pos, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE));
						count++;
						break;
					case ACACIA:
						handler.setBlock(pos, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.ACACIA));
						count++;
						break;
					case DARK_OAK:
						handler.setBlock(pos, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.DARK_OAK));
						count++;
						break;
					default:
						break;
				}
			} else if (blockState.getBlock() instanceof BlockPurpurSlab && ((BlockPurpurSlab) blockState.getBlock()).isDouble()) {
				handler.setBlock(pos, Blocks.PURPUR_BLOCK.getDefaultState());
				count++;
			} else if (blockState.getBlock() instanceof BlockStoneSlab && blockState.getProperties().containsKey(BlockStoneSlab.VARIANT) && blockState.getValue(BlockStoneSlab.VARIANT) == BlockStoneSlab.EnumType.WOOD) {
				handler.setBlock(pos, Blocks.WOODEN_SLAB.getDefaultState().withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.OAK).withProperty(BlockSlab.HALF, blockState.getValue(BlockStoneSlab.HALF)));
				count++;
			}
		}
		handler.autoCancel = true;
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
