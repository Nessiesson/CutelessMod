package net.dugged.cleancuteless.chunk_display.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;

public abstract class GuiSubWindow extends GuiScreen {

	private final List<String> stackTrace;
	protected String title;
	protected GuiScreen parentScreen;
	protected GuiScreen backgroundScreen;

	public GuiSubWindow(String title, GuiScreen parentScreen, GuiScreen backgroundScreen, List<String> stackTrace) {
		this.title = title;
		this.parentScreen = parentScreen;
		this.backgroundScreen = backgroundScreen;
		this.stackTrace = stackTrace;
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);
		backgroundScreen.setWorldAndResolution(mc, width, height);
	}

	protected void layoutButtons(GuiButton... buttons) {
		int buttonX = getSubWindowLeft() + 5;
		int buttonY = getSubWindowBottom() - (20 + getFooterHeight()) / 2;
		int buttonWidth = (getSubWindowRight() - getSubWindowLeft() - 5 * (buttons.length + 1)) / buttons.length;

		for (GuiButton b : buttons) {
			b.x = buttonX;
			b.y = buttonY;
			b.setWidth(buttonWidth);
			buttonX += 5 + buttonWidth;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		backgroundScreen.drawScreen(-1, -1, partialTicks); // Spoof mouse coords so buttons don't highlight
		GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);

		Gui.drawRect(getSubWindowLeft(), getSubWindowTop(), getSubWindowRight(), getSubWindowBottom(), 0xD8101010);
		Gui.drawRect(getSubWindowLeft(), getSubWindowTop(), getSubWindowRight(), getSubWindowTop() + 1, 0xFF707070);
		Gui.drawRect(getSubWindowLeft(), getSubWindowBottom() - 1, getSubWindowRight(), getSubWindowBottom(), 0xFF707070);
		Gui.drawRect(getSubWindowLeft(), getSubWindowTop(), getSubWindowLeft() + 1, getSubWindowBottom(), 0xFF707070);
		Gui.drawRect(getSubWindowRight() - 1, getSubWindowTop(), getSubWindowRight(), getSubWindowBottom(), 0xFF707070);
		Gui.drawRect(getSubWindowLeft(), getSubWindowBottom() - getFooterHeight(), getSubWindowRight(), getSubWindowBottom() - getFooterHeight() + 1, 0xFF3A3A3A);

		fontRenderer.drawStringWithShadow(title, (getSubWindowLeft() + getSubWindowRight()) / 2 - fontRenderer.getStringWidth(title) / 2, getSubWindowTop() + 6, 0xFFFFFFFF);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (selectedButton == null) {
			if (mouseButton == 0) {
				mc.displayGuiScreen(parentScreen);
				if (parentScreen instanceof GuiChunkGrid) {
					((GuiChunkGrid) parentScreen).consumeLeftClick();
				}
			} else if (mouseButton == 1) {
				if (stackTrace != null && !(this instanceof GuiShowStackTrace)) {
					mc.displayGuiScreen(new GuiShowStackTrace(this, backgroundScreen, stackTrace));
				}
			}
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == Keyboard.KEY_ESCAPE)
			mc.displayGuiScreen(parentScreen);
		else
			super.keyTyped(typedChar, keyCode);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return backgroundScreen.doesGuiPauseGame();
	}

	protected int getSubWindowLeft() {
		return (int) (width * 0.1);
	}

	protected int getSubWindowRight() {
		return (int) (width * 0.9);
	}

	protected int getSubWindowTop() {
		return (int) (height * 0.1);
	}

	protected int getSubWindowBottom() {
		return (int) (height * 0.9);
	}

	protected int getFooterHeight() {
		return 30;
	}
}
