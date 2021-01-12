package net.dugged.cutelessmod.clientcommands.worldedit;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class WorldEdit {
	private static final Minecraft mc = Minecraft.getMinecraft();
	public static BlockPos posA = null;
	public static BlockPos posB = null;

	public static boolean hasSelection() {
		return posA != null && posB != null;
	}

	public static BlockPos playerPos() {
		return new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
	}

	public static EnumFacing getLookingDirection() {
		Entity entity = mc.getRenderViewEntity();
		EnumFacing enumfacing = entity.getHorizontalFacing();
		if (entity.rotationPitch > 45) {
			enumfacing = EnumFacing.DOWN;
		} else if (entity.rotationPitch < -45) {
			enumfacing = EnumFacing.UP;
		}
		return enumfacing;
	}

	public static BlockPos offsetLookingDirection(BlockPos pos, int offset) {
		return new BlockPos(pos.offset(getLookingDirection(), offset));
	}

	public static void sendMessage(String msg) {
		sendMessage(new TextComponentTranslation(msg));
	}

	public static void sendMessage(TextComponentTranslation msg) {
		msg.getStyle().setColor(TextFormatting.LIGHT_PURPLE);
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(msg);
	}

	public static BlockPos minPos() {
		return new BlockPos(Math.min(posA.getX(), posB.getX()), Math.min(posA.getY(), posB.getY()), Math.min(posA.getZ(), posB.getZ()));
	}

	public static BlockPos maxPos() {
		return new BlockPos(Math.max(posA.getX(), posB.getX()), Math.max(posA.getY(), posB.getY()), Math.max(posA.getZ(), posB.getZ()));
	}

	public static int widthX() {
		return maxPos().getX() - minPos().getX() + 1;
	}

	public static int widthY() {
		return maxPos().getY() - minPos().getY() + 1;
	}

	public static int widthZ() {
		return maxPos().getZ() - minPos().getZ() + 1;
	}

	public static long volume() {
		return widthX() * widthY() * widthZ();
	}

	public static boolean isOneByOne() {
		return widthX() == 1 && widthY() == 1 && widthZ() == 1;
	}

	public static boolean checkSphere(final double x, final double y, final double z, final double r) {
		return x * x + y * y + z * z <= r * r;
	}

	public static boolean checkCircle(final double x, final double z, final double r) {
		return x * x + z * z <= r * r;
	}

	public static int maxWidth() {
		return Math.max(widthX(), Math.max(widthY(), widthZ()));
	}

	public static IBlockState flipBlockstate(IBlockState blockState, EnumFacing.Axis axis) {
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
}
