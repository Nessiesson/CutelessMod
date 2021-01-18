package net.dugged.cutelessmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuRecipient;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiCompass extends Gui implements ISpectatorMenuRecipient {

	private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
	public static final ResourceLocation SPECTATOR_WIDGETS = new ResourceLocation("textures/gui/spectator_widgets.png");
	private final Minecraft mc;
	private long lastSelectionTime;
	private SpectatorMenu menu;

	GuiCompass(Minecraft mcIn) {
		mc = mcIn;
	}

	private float getHotbarAlpha() {
		long i = lastSelectionTime - Minecraft.getSystemTime() + 3000L;
		return MathHelper.clamp((float) i / 2000.0F, 0.0F, 1.0F);
	}

	public void renderTooltip(ScaledResolution scaledResolution) {
		if (menu != null) {
			float f = getHotbarAlpha();
			if (f <= 0.0F) {
				menu.exit();
			} else {
				int i = scaledResolution.getScaledWidth() / 2;
				float f1 = zLevel;
				zLevel = -90.0F;
				SpectatorDetails spectatordetails = menu.getCurrentPage();
				renderPage(scaledResolution, f, i, scaledResolution.getScaledHeight() - 46, spectatordetails);
				zLevel = f1;
			}
		}
	}

	public void exit() {
		menu = null;
		lastSelectionTime = 0L;
	}

	protected void renderPage(ScaledResolution scaledResolution, float alpha, int x, int y, SpectatorDetails spectatorDetails) {
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
		mc.getTextureManager().bindTexture(WIDGETS);
		drawTexturedModalRect((float) (x - 91), y, 0, 0, 182, 22);
		if (spectatorDetails.getSelectedSlot() >= 0) {
			drawTexturedModalRect((float) (x - 91 - 1 + spectatorDetails.getSelectedSlot() * 20), y - 1, 0, 22, 24, 22);
		}
		RenderHelper.enableGUIStandardItemLighting();
		for (int i = 0; i < 9; ++i) {
			renderSlot(scaledResolution.getScaledWidth() / 2 - 90 + i * 20 + 2, y + 3, alpha, spectatorDetails.getObject(i));
		}
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
	}

	private void renderSlot(int x, int y, float alpha, ISpectatorMenuObject menuObject) {
		mc.getTextureManager().bindTexture(SPECTATOR_WIDGETS);
		if (menuObject != SpectatorMenu.EMPTY_SLOT) {
			int i = (int) (alpha * 255.0F);
			GlStateManager.pushMatrix();
			GlStateManager.translate((float) x, y, 0.0F);
			float f = menuObject.isEnabled() ? 1.0F : 0.25F;
			GlStateManager.color(f, f, f, alpha);
			menuObject.renderIcon(f, i);
			GlStateManager.popMatrix();
		}
	}

	public void renderSelectedItem(ScaledResolution scaledResolution) {
		int i = (int) (getHotbarAlpha() * 255.0F);
		if (i > 3 && menu != null) {
			ISpectatorMenuObject ispectatormenuobject = menu.getSelectedItem();
			String s = ispectatormenuobject == SpectatorMenu.EMPTY_SLOT ? menu.getSelectedCategory().getPrompt().getFormattedText() : ispectatormenuobject.getSpectatorName().getFormattedText();
			int x = (scaledResolution.getScaledWidth() - mc.fontRenderer.getStringWidth(s)) / 2;
			int y = scaledResolution.getScaledHeight() - 59;
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			mc.fontRenderer.drawStringWithShadow(s, (float) x, (float) y, 16777215 + (i << 24));
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	public void onSpectatorMenuClosed(SpectatorMenu menu) {
		exit();
	}

	public boolean isMenuActive() {
		return menu != null;
	}

	public void onMouseScroll(int scrollValue) {
		scrollValue = -(scrollValue < 0 ? -1 : 1);
		int i;
		for (i = menu.getSelectedSlot() + scrollValue; i >= 0 && i <= 8 && (menu.getItem(i) == SpectatorMenu.EMPTY_SLOT || !menu.getItem(i).isEnabled()); i += scrollValue) {
			;
		}

		if (i >= 0 && i <= 8) {
			menu.selectSlot(i);
			lastSelectionTime = Minecraft.getSystemTime();
		}
	}

	public void onRightClick() {
		lastSelectionTime = Minecraft.getSystemTime();
		if (isMenuActive()) {
			int i = menu.getSelectedSlot();

			if (i != -1) {
				menu.selectSlot(i);
			}
		} else {
			menu = new SpectatorMenu(this);
		}
	}

	public void onMiddleClick() {
		lastSelectionTime = Minecraft.getSystemTime();
		if (isMenuActive()) {
			int i = menu.getSelectedSlot();
			if (i != -1) {
				menu.selectSlot(i);
			}
		} else {
			menu = new SpectatorMenu(this);
		}
	}
}