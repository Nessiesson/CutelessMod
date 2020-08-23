package net.dugged.cutelessmod.mixins.liteloader;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
	@Group(name = "CutelessModAlwaysPickBlockMaxStack")
	@ModifyVariable(method = "middleClickMouse", ordinal = 0, index = 3, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;sendSlotPacket(Lnet/minecraft/item/ItemStack;I)V"))
	private ItemStack maxStackSize(final ItemStack stack) {
		if (Configuration.alwaysPickBlockMaxStack) {
			stack.setCount(stack.getMaxStackSize());
		}

		return stack;
	}
}
