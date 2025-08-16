package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;

@Mixin(GuiChat.class)
public abstract class MixinGuiChat {
	@ModifyConstant(method = "initGui", constant = @Constant(intValue = 256))
	private int increaseLimit(final int orig) {
		return Configuration.extendedChat ? 2048 : orig;
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	private void cutelessmod$clickToCopyChat(final int mouseX, final int mouseY, final int mouseButton, final CallbackInfo ci) {
		if (!Configuration.clickToCopyChat) {
			return;
		}

		if (mouseButton == 0) {
			final ITextComponent component = cutelessmod$getChatLine();
			if (component != null) {
				if (GuiScreen.isShiftKeyDown()) {
					final String text = GuiScreen.isCtrlKeyDown() ? component.getFormattedText() : component.getUnformattedText();
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
					Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Copied chat message to clipboard!" + (GuiScreen.isCtrlKeyDown() ? " (With formatting)" : ""), false);
					ci.cancel();
				}
			}
		}
	}

	@Unique
	private static ITextComponent cutelessmod$getChatLine() {
		final Minecraft mc = Minecraft.getMinecraft();
		final GuiNewChat chat = mc.ingameGUI.getChatGUI();
		if (!chat.getChatOpen()) {
			return null;
		}

		final ScaledResolution scaledresolution = new ScaledResolution(mc);
		final int factor = scaledresolution.getScaleFactor();
		final float scale = chat.getChatScale();
		final int scaledX = MathHelper.floor((float) (Mouse.getX() / factor - 2) / scale);
		final int scaledY = MathHelper.floor((float) (Mouse.getY() / factor - 40) / scale);
		if (scaledX < 0 || scaledY < 0) {
			return null;
		}

		final List<ChatLine> drawnChatLines = ((IGuiNewChat) chat).getDrawnChatLines();
		final int lineCount = Math.min(chat.getLineCount(), drawnChatLines.size());
		final int fontHeight = mc.fontRenderer.FONT_HEIGHT;
		if (scaledX > MathHelper.floor((float) chat.getChatWidth() / chat.getChatScale()) || scaledY >= fontHeight * lineCount + lineCount) {
			return null;
		}

		final int idc = scaledY / fontHeight + ((IGuiNewChat) chat).getScrollPos();
		if (idc >= 0 && idc < drawnChatLines.size()) {
			return drawnChatLines.get(idc).getChatComponent();
		}

		return null;
	}
}
