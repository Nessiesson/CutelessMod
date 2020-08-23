package net.dugged.cutelessmod;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

public class GuiEmptyScreen extends GuiScreen {
	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		if (keyCode == 1) {
			this.mc.displayGuiScreen(null);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawRect(this.width - 4, this.height - 4, this.width - 2, this.height - 2, TextFormatting.DARK_RED.getColorIndex());
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
