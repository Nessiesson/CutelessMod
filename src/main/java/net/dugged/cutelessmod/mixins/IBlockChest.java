package net.dugged.cutelessmod.mixins;

import net.minecraft.block.BlockChest;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockChest.class)
public interface IBlockChest {
	@Accessor("NORTH_CHEST_AABB")
	static AxisAlignedBB getNorthAABB() {
		throw new UnsupportedOperationException();
	}

	@Accessor("SOUTH_CHEST_AABB")
	static AxisAlignedBB getSouthAABB() {
		throw new UnsupportedOperationException();
	}

	@Accessor("WEST_CHEST_AABB")
	static AxisAlignedBB getWestAABB() {
		throw new UnsupportedOperationException();
	}

	@Accessor("EAST_CHEST_AABB")
	static AxisAlignedBB getEastAABB() {
		throw new UnsupportedOperationException();
	}

	@Accessor("NOT_CONNECTED_AABB")
	static AxisAlignedBB getNoneAABB() {
		throw new UnsupportedOperationException();
	}
}
