package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityBeacon.class)
public abstract class MixinTileEntityBeacon extends TileEntityLockable {
	@Shadow
	private int levels;
	@Shadow
	private boolean isComplete;

	//TODO: Decide which kind of highlighting is wanted for beacons...
	@Inject(method = "update", at = @At("HEAD"), cancellable = true)
	private void onUpdate(final CallbackInfo ci) {
		if (CutelessMod.toggleBeaconArea && this.isComplete && this.levels > 0) {
			final double radius = this.levels * 10 + 10;
			final int x = this.pos.getX();
			final int y = this.pos.getY();
			final int z = this.pos.getZ();
			final BlockPos min = new BlockPos(x - radius, y - radius, z - radius);
			final BlockPos max = new BlockPos(x + radius + 1, this.world.getHeight(), z + radius + 1);
			final AxisAlignedBB bb = new AxisAlignedBB(min, max);
			CutelessMod.beaconsToRender.put(bb, 5);
		}
	}
}
