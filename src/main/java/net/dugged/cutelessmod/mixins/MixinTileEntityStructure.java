package net.dugged.cutelessmod.mixins;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(TileEntityStructure.class)
public abstract class MixinTileEntityStructure extends TileEntity {

	@ModifyConstant(method = "readFromNBT", constant = @Constant(intValue = -32), expect = 3)
	public int modifyNeg32(int orig) {
		return Integer.MIN_VALUE;
	}

	@ModifyConstant(method = "readFromNBT", constant = @Constant(intValue = 32), expect = 6)
	public int modify32(int orig) {
		return Integer.MAX_VALUE;
	}

}