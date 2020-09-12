package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.gui.GuiChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiChat.class)
public abstract class MixinGuiChat {
	@ModifyConstant(method = "initGui", constant = @Constant(intValue = 256))
	private int increaseLimit(final int orig) {
		return Configuration.extendedChat ? 2048 : orig;
	}
}
