package net.dugged.cutelessmod;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

public enum DoubleChestSide implements IStringSerializable {
	LEFT("left"),
	RIGHT("right"),
	NONE("none");

	private final String name;
	public static final PropertyEnum<DoubleChestSide> AABB = PropertyEnum.create("aabb", DoubleChestSide.class);

	DoubleChestSide(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public static DoubleChestSide getSide(final EnumFacing chestFace, final EnumFacing connection) {
		if (chestFace.getAxis().isVertical() || connection.getAxis().isVertical()) {
			return NONE;
		}

		if ((chestFace.getHorizontalIndex() + 1) % 4 == connection.getHorizontalIndex()) {
			return RIGHT;
		}

		if ((chestFace.getHorizontalIndex() + 3) % 4 == connection.getHorizontalIndex()) {
			return LEFT;
		}

		return NONE;
	}
}
