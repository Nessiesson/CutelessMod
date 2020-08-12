package net.dugged.cutelessmod.mixins;

import net.minecraft.block.BlockConcretePowder;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockConcretePowder.class)
public abstract class MixinBlockConcretePowder extends BlockFalling {
	@Shadow
	@Final
	public static PropertyEnum<EnumDyeColor> COLOR;

	@Override
	public int getDustColor(final IBlockState state) {
		return state.getValue(COLOR).getColorValue();
	}
}
