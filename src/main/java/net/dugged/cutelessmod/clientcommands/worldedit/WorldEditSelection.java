package net.dugged.cutelessmod.clientcommands.worldedit;

import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashMap;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

public class WorldEditSelection {
	private final HashMap<Position, BlockPos> positions = new HashMap<>();
	private final SelectionType type;

	public WorldEditSelection(SelectionType type) {
		this.type = type;
	}

	public static SelectionType getTypeForMaterial(Item.ToolMaterial material) {
		switch (material) {
			case WOOD:
				return SelectionType.WOOD;
			case STONE:
				return SelectionType.STONE;
			case IRON:
				return SelectionType.IRON;
			case DIAMOND:
				return SelectionType.DIAMOND;
			case GOLD:
				return SelectionType.GOLD;
		}
		return null;
	}

	public BlockPos getPos(Position pos) {
		return positions.getOrDefault(pos, null);
	}

	public void setPos(Position pos, BlockPos blockPos) {
		positions.put(pos, blockPos);
		WorldEdit.selections.put(type, this); // Update position
	}

	public boolean isCompleted() {
		return positions.containsKey(A) && positions.get(A) != null && positions.containsKey(B) && positions.get(B) != null;
	}

	public AxisAlignedBB getBB() {
		return new AxisAlignedBB(positions.get(A), positions.get(B));
	}

	public BlockPos minPos() {
		return new BlockPos(getBB().minX, getBB().minY, getBB().minZ);
	}

	public BlockPos maxPos() {
		return new BlockPos(getBB().maxX, getBB().maxY, getBB().maxZ);
	}

	public int widthX() {
		return maxPos().getX() - minPos().getX() + 1;
	}

	public int widthY() {
		return maxPos().getY() - minPos().getY() + 1;
	}

	public int widthZ() {
		return maxPos().getZ() - minPos().getZ() + 1;
	}

	public long volume() {
		return widthX() * widthY() * widthZ();
	}

	public boolean isOneByOne() {
		return widthX() == 1 && widthY() == 1 && widthZ() == 1;
	}

	public enum SelectionType {
		WOOD(134, 101, 38),
		STONE(154, 154, 154),
		IRON(255, 255, 255),
		DIAMOND(51, 235, 203),
		GOLD(234, 238, 87);

		private final Color color;

		SelectionType(int r, int g, int b) {
			this.color = new Color(r, g, b);
		}

		public Color getColor() {
			return color;
		}
	}

	public enum Position {
		A(255, 0, 0),
		B(0, 0, 255);

		private final Color color;

		Position(int r, int g, int b) {
			this.color = new Color(r, g, b);
		}

		public Color getColor() {
			return color;
		}
	}
}
