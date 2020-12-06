package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
import net.minecraft.block.*;
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

	private IBlockState flipBlockstate(IBlockState blockState, EnumFacing.Axis axis) {
		if (blockState == null) {
			return null;
		}
		if (axis == EnumFacing.Axis.Y && blockState.getProperties().containsKey(BlockSlab.HALF)) {
			if (blockState.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM) {
				return blockState.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP);
			} else if (blockState.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP) {
				return blockState.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM);
			}
		}

		if (axis == EnumFacing.Axis.Y && blockState.getProperties().containsKey(BlockStairs.HALF)) {
			if (blockState.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.BOTTOM) {
				return blockState.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
			} else if (blockState.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP) {
				return blockState.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.BOTTOM);
			}
		}

		if (blockState.getProperties().containsKey(BlockLever.FACING)) {
			switch (axis) {
				case X:
					if (blockState.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.WEST) {
						return blockState.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.EAST);
					} else if (blockState.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.EAST) {
						return blockState.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.WEST);
					}
					break;
				case Y:
					if (blockState.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.DOWN_X) {
						return blockState.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.UP_X);
					} else if (blockState.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.DOWN_Z) {
						return blockState.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.UP_Z);
					} else if (blockState.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.UP_X) {
						return blockState.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.DOWN_X);
					} else if (blockState.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.UP_Z) {
						return blockState.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.DOWN_Z);
					}
					break;
				case Z:
					if (blockState.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.NORTH) {
						return blockState.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.SOUTH);
					} else if (blockState.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.SOUTH) {
						return blockState.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.NORTH);
					}
					break;
			}
		}

		if (blockState.getProperties().containsKey(BlockHorizontal.FACING)) {
			switch (axis) {
				case X:
					if (blockState.getValue(BlockHorizontal.FACING) == EnumFacing.WEST) {
						return blockState.withProperty(BlockHorizontal.FACING, EnumFacing.EAST);
					} else if (blockState.getValue(BlockHorizontal.FACING) == EnumFacing.EAST) {
						return blockState.withProperty(BlockHorizontal.FACING, EnumFacing.WEST);
					}
					break;
				case Z:
					if (blockState.getValue(BlockHorizontal.FACING) == EnumFacing.NORTH) {
						return blockState.withProperty(BlockHorizontal.FACING, EnumFacing.SOUTH);
					} else if (blockState.getValue(BlockHorizontal.FACING) == EnumFacing.SOUTH) {
						return blockState.withProperty(BlockHorizontal.FACING, EnumFacing.NORTH);
					}
					break;
			}
		}

		if (blockState.getProperties().containsKey(BlockDirectional.FACING)) {
			switch (axis) {
				case X:
					if (blockState.getValue(BlockDirectional.FACING) == EnumFacing.WEST) {
						return blockState.withProperty(BlockDirectional.FACING, EnumFacing.EAST);
					} else if (blockState.getValue(BlockDirectional.FACING) == EnumFacing.EAST) {
						return blockState.withProperty(BlockDirectional.FACING, EnumFacing.WEST);
					}
					break;
				case Y:
					if (blockState.getValue(BlockDirectional.FACING) == EnumFacing.DOWN) {
						return blockState.withProperty(BlockDirectional.FACING, EnumFacing.UP);
					} else if (blockState.getValue(BlockDirectional.FACING) == EnumFacing.UP) {
						return blockState.withProperty(BlockDirectional.FACING, EnumFacing.DOWN);
					}
					break;
				case Z:
					if (blockState.getValue(BlockDirectional.FACING) == EnumFacing.NORTH) {
						return blockState.withProperty(BlockDirectional.FACING, EnumFacing.SOUTH);
					} else if (blockState.getValue(BlockDirectional.FACING) == EnumFacing.SOUTH) {
						return blockState.withProperty(BlockDirectional.FACING, EnumFacing.NORTH);
					}
					break;
			}
		}
		return blockState;
	}

	private void flipSelection(World world) {
		EnumFacing direction = WorldEdit.getLookingDirection();
		Map<BlockPos, IBlockState> blockList = new HashMap<>();
		for (int x = 0; x < WorldEdit.widthX(); x++) {
			for (int y = 0; y < WorldEdit.widthY(); y++) {
				for (int z = 0; z < WorldEdit.widthZ(); z++) {
					IBlockState blockState;
					if (direction.getAxis() == EnumFacing.Axis.Y) {
						blockState = world.getBlockState(WorldEdit.minPos().add(x, y, z));
						blockList.put(WorldEdit.minPos().add(x, WorldEdit.widthY() - y - 1, z), flipBlockstate(blockState, direction.getAxis()));
					} else if (direction.getAxis() == EnumFacing.Axis.Z) {
						blockState = world.getBlockState(WorldEdit.minPos().add(x, y, z));
						blockList.put(WorldEdit.minPos().add(x, y, WorldEdit.widthZ() - z - 1), flipBlockstate(blockState, direction.getAxis()));
					} else {
						blockState = world.getBlockState(WorldEdit.minPos().add(x, y, z));
						blockList.put(WorldEdit.minPos().add(WorldEdit.widthX() - x - 1, y, z), flipBlockstate(blockState, direction.getAxis()));
					}
				}
			}
		}
		HandlerSetBlock handler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world);
		handler.isWorldEditHandler = true;
		handler.setBlocks(blockList);
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
