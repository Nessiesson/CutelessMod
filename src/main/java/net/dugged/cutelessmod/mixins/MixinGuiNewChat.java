package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.CompactChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat {
	private static final CompactChat compactChat = new CompactChat();

	//TODO: Chat
	@Inject(method = "printChatMessage", at = @At("HEAD"), cancellable = true)
	private void onChat(ITextComponent component, CallbackInfo ci) {
		if (compactChat.onChat(component)) {
			ci.cancel();
		}
	}
}
