package net.dugged.cutelessmod.mixins;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Material.class)
public class MixinMaterial {
	@Shadow
	@Final @Mutable public static Material CRAFTED_SNOW = ((IMaterial)(new Material(MapColor.SNOW))).makeTranslucent();
}
