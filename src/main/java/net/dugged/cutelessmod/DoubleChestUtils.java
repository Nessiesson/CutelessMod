package net.dugged.cutelessmod;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

public enum DoubleChestUtils implements IStringSerializable {
	NONE("none"),
	LEFT("left"),
	RIGHT("right");

	private final String name;
	public static final PropertyEnum<DoubleChestUtils> AABB = PropertyEnum.create("aabb", DoubleChestUtils.class);

	DoubleChestUtils(String name) {
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

	public static DoubleChestUtils getSide(final EnumFacing chestFace, final EnumFacing connection) {
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
