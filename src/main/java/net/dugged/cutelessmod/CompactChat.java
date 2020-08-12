package net.dugged.cutelessmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

//TODO: Fix chat
// Inspired/stolen by https://github.com/Sk1erLLC/CompactChat
public class CompactChat {
	private ITextComponent lastMessage = new TextComponentString("");
	private int amount;
	private int line;

	public boolean onChat(final ITextComponent message) {
		final GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
		if (message.getUnformattedText().equals(this.lastMessage.getUnformattedText())) {
			chat.deleteChatLine(this.line);
			final ITextComponent component = new TextComponentString(String.format(" (%d)", ++this.amount));
			component.setStyle(new Style().setColor(TextFormatting.GRAY));
			message.appendSibling(component);
		} else {
			this.amount = 1;
			this.lastMessage = message;
		}

		chat.printChatMessageWithOptionalDeletion(message, ++this.line);
		if (this.line > 256) {
			this.line = 0;
		}

		return true;
	}
}
