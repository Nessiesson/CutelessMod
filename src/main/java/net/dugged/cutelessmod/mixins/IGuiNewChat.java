package net.dugged.cutelessmod.mixins;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(GuiNewChat.class)
public interface IGuiNewChat {
	@Mutable
	@Accessor
	List<ChatLine> getChatLines();

	@Mutable
	@Accessor
	List<ChatLine> getDrawnChatLines();

	@Accessor
	int getScrollPos();
}
