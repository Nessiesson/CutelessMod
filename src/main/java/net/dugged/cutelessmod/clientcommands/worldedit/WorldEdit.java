package net.dugged.cutelessmod.clientcommands.worldedit;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.dugged.cutelessmod.clientcommands.mixins.IItemSword;
import net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.SelectionType;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class WorldEdit {

	private static final Minecraft mc = Minecraft.getMinecraft();
	public static List<BrushBase> brushes = new ArrayList<>();
	public static HashMap<Item, BrushBase> currentBrushes = new HashMap<>();
	protected static HashMap<WorldEditSelection.SelectionType, WorldEditSelection> selections = new HashMap<>();

	public static BlockPos getPos(WorldEditSelection.SelectionType type,
		WorldEditSelection.Position pos) {
		WorldEditSelection selection = selections.getOrDefault(type, null);
		if (selection != null) {
			return selection.getPos(pos);
		} else {
			return null;
		}
	}

	public static BlockPos getPos(Item.ToolMaterial material, WorldEditSelection.Position pos) {
		return getPos(WorldEditSelection.getTypeForMaterial(material), pos);
	}

	public static void setPos(WorldEditSelection.SelectionType type,
		WorldEditSelection.Position pos, BlockPos blockPos) {
		if (selections.containsKey(type)) {
			WorldEditSelection selection = selections.get(type);
			selection.setPos(pos, blockPos);
			selections.put(type, selection);
		} else {
			WorldEditSelection selection = new WorldEditSelection(type);
			selection.setPos(pos, blockPos);
			selections.put(type, selection);
		}
	}

	public static void setPos(Item.ToolMaterial material, WorldEditSelection.Position pos,
		BlockPos blockPos) {
		setPos(WorldEditSelection.getTypeForMaterial(material), pos, blockPos);
	}

	public static WorldEditSelection getCurrentSelection() {
		Item itemInHand = mc.player.getHeldItemMainhand().getItem();
		if (mc.player.isCreative() && itemInHand instanceof ItemSword) {
			return selections.get(
				WorldEditSelection.getTypeForMaterial(((IItemSword) itemInHand).getMaterial()));
		}
		if (!mc.player.isCreative() && selections.containsKey(SelectionType.GOLD)) {
			return selections.get(SelectionType.GOLD);
		}
		return null;
	}

	public static boolean hasCurrentSelection() {
		return getCurrentSelection() != null && getCurrentSelection().isCompleted();
	}

	public static void clearAllSelections() {
		for (WorldEditSelection.SelectionType type : selections.keySet()) {
			setPos(type, A, null);
			setPos(type, B, null);
		}
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

	public static boolean checkSphere(final double x, final double y, final double z,
		final double r) {
		return x * x + y * y + z * z <= r * r;
	}

	public static boolean checkCircle(final double x, final double z, final double r) {
		return x * x + z * z <= r * r;
	}

	public static IBlockState flipBlockstate(IBlockState blockState, EnumFacing.Axis axis) {
		if (blockState == null) {
			return null;
		}
		if (axis == EnumFacing.Axis.X) {
			return blockState.withMirror(Mirror.FRONT_BACK);
		} else if (axis == EnumFacing.Axis.Z) {
			return blockState.withMirror(Mirror.LEFT_RIGHT);
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
		if (axis == EnumFacing.Axis.Y && blockState.getProperties()
			.containsKey(BlockTrapDoor.HALF)) {
			if (blockState.getValue(BlockTrapDoor.HALF) == BlockTrapDoor.DoorHalf.BOTTOM) {
				return blockState.withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP);
			} else if (blockState.getValue(BlockTrapDoor.HALF) == BlockTrapDoor.DoorHalf.TOP) {
				return blockState.withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.BOTTOM);
			}
		}
		if (axis == EnumFacing.Axis.Y && blockState.getProperties()
			.containsKey(BlockLever.FACING)) {
			if (blockState.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.DOWN_X) {
				return blockState.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.UP_X);
			} else if (blockState.getValue(BlockLever.FACING)
				== BlockLever.EnumOrientation.DOWN_Z) {
				return blockState.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.UP_Z);
			} else if (blockState.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.UP_X) {
				return blockState.withProperty(BlockLever.FACING,
					BlockLever.EnumOrientation.DOWN_X);
			} else if (blockState.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.UP_Z) {
				return blockState.withProperty(BlockLever.FACING,
					BlockLever.EnumOrientation.DOWN_Z);
			}
		}
		if (axis == EnumFacing.Axis.Y && blockState.getProperties()
			.containsKey(BlockDirectional.FACING)) {
			if (blockState.getValue(BlockDirectional.FACING) == EnumFacing.DOWN) {
				return blockState.withProperty(BlockDirectional.FACING, EnumFacing.UP);
			} else if (blockState.getValue(BlockDirectional.FACING) == EnumFacing.UP) {
				return blockState.withProperty(BlockDirectional.FACING, EnumFacing.DOWN);
			}
		}
		return blockState;
	}

	public static BrushBase getBrush(String name) {
		for (BrushBase brush : WorldEdit.brushes) {
			if (brush.getName().equals(name)) {
				return brush;
			}
		}
		return null;
	}
}
