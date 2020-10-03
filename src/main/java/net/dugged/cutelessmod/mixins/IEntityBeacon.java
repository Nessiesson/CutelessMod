package net.dugged.cutelessmod.mixins;

import net.minecraft.tileentity.TileEntityBeacon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TileEntityBeacon.class)
public interface IEntityBeacon {
	@Accessor
	boolean getIsComplete();
}