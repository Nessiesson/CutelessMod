package net.dugged.cutelessmod.mixins.forge;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ForgeHooks.class)
public abstract class MixinForgeHooks {
	@Group(name = "CutelessModAlwaysPickBlockMaxStack")
	@ModifyVariable(method = "onPickBlock", ordinal = 0, index = 3, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;sendSlotPacket(Lnet/minecraft/item/ItemStack;I)V"))
	private static ItemStack maxStackSize(final ItemStack stack) {
		if (Configuration.alwaysPickBlockMaxStack) {
			stack.setCount(stack.getMaxStackSize());
		}

		return stack;
	}
}
