package net.dugged.cutelessmod.mixins;

import net.minecraft.block.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Material.class)
public interface IMaterial {
	@Invoker("setTranslucent")
	Material makeTranslucent();
}
