package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.CutelessMod;
import net.dugged.cutelessmod.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemCompass;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame extends Gui {

	private static final ResourceLocation EXTENDED_HOTBAR_PATH = new ResourceLocation(Reference.MODID, "textures/extended_hotbar.png");

	@Shadow
	protected abstract void renderHotbarItem(int x, int y, float partialTicks, EntityPlayer player, ItemStack stack);

	@Shadow
	@Final
	protected Minecraft mc;
	@Shadow
	@Final
	protected static ResourceLocation WIDGETS_TEX_PATH;
	private long lastTick = 0;

	@Inject(method = "setOverlayMessage(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true)
	private void parseDuggedMSPT(String message, final boolean animateColor, final CallbackInfo ci) {
		final String s = message.replaceAll("(\u00A7a|\u00A7r)", "");
		if (message.matches("\u00A7a\\d*\u00A7r") && StringUtils.isNumeric(s)) {
			CutelessMod.overlayTimer = 60;
			CutelessMod.mspt = Integer.parseInt(s);
			ci.cancel();
		}
	}

	@Inject(method = "updateTick", at = @At("RETURN"))
	public void saveChat(CallbackInfo ci) {
		final Minecraft mc = Minecraft.getMinecraft();
		// insane performance benefit by only calling every 10 ticks
		if (CutelessMod.tickCounter > lastTick && mc.player != null) {
			lastTick = CutelessMod.tickCounter + 10;
			final String currentServer = CutelessMod.currentServer != null ? CutelessMod.currentServer.serverIP : "SINGLEPLAYER";
			final GuiNewChat chat = mc.ingameGUI.getChatGUI();
			CutelessMod.tabCompleteHistory.put(currentServer, new ArrayList<>(chat.getSentMessages()));
			CutelessMod.chatHistory.put(currentServer, new ArrayList<>(((IGuiNewChat) chat).getChatLines()));
		}
	}

	@Inject(method = "renderHotbar", at = @At("RETURN"))
	public void renderCompassMenu(ScaledResolution scaledResolution, float partialTicks, CallbackInfo ci) {
		final Minecraft mc = Minecraft.getMinecraft();
		if (Configuration.worldeditCompass && mc.player.isCreative() && mc.player.getHeldItemMainhand().getItem() instanceof ItemCompass) {
			CutelessMod.guiCompass.renderTooltip(scaledResolution);
			CutelessMod.guiCompass.renderSelectedItem(scaledResolution);
		} else if (CutelessMod.guiCompass.isMenuActive()) {
			CutelessMod.guiCompass.exit();
		}
	}

	@Redirect(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;drawTexturedModalRect(IIIIII)V", ordinal = 1))
	private void modifySelectedItem(GuiIngame instance, int x, int y, int textureX, int textureY, int width, int height) {
		if (Configuration.extendedCreativeHotbar && mc.player.isCreative()) {
			EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
			int slot = CutelessMod.hotbarSlot;
			x -= player.inventory.currentItem * 20;
			x += slot * 20;
			if (CutelessMod.hotbarSlot >= 9) {
				if (player.getPrimaryHand().opposite() == EnumHandSide.LEFT) {
					x++;
				} else {
					x -= slot * 20;
					x = x - 82 + (CutelessMod.hotbarSlot % 9) * 20;
				}
			}
		}
		this.drawTexturedModalRect(x, y, textureX, textureY, width, height);
	}

//	@Redirect(method = "renderAttackIndicator", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;drawTexturedModalRect(IIIIII)V"))
//	private void shiftAttackIndicator(GuiIngame instance, int x, int y, int textureX, int textureY, int width, int height) {
//		if (Configuration.extendedCreativeHotbar && mc.player.isCreative() && mc.player.getPrimaryHand().opposite() == EnumHandSide.LEFT) {
//			x += 182;
//		}
//		drawTexturedModalRect(x, y, textureX, textureY, width, height);
//	}

	@Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;drawTexturedModalRect(IIIIII)V", shift = At.Shift.AFTER, ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private void alterTexture(ScaledResolution sr, float l, CallbackInfo ci, EntityPlayer player, ItemStack itemStack, EnumHandSide handSide, int i, float f, int j, int k) {
		if (Configuration.extendedCreativeHotbar && player.isCreative()) {
			mc.getTextureManager().bindTexture(EXTENDED_HOTBAR_PATH);
			if (handSide == EnumHandSide.LEFT) {
				this.drawTexturedModalRect(i + 91, sr.getScaledHeight() - 22, 0, 0, 81, 22);
			} else {
				this.drawTexturedModalRect(i - 91 - 81, sr.getScaledHeight() - 22, 0, 22, 81, 22);
			}
			mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
		}
	}

	@Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderHelper;enableGUIStandardItemLighting()V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void modifyHotbar(ScaledResolution sr, float partialTicks, CallbackInfo ci, EntityPlayer player, ItemStack itemStack, EnumHandSide handSide, int i, float f, int j, int k) {
		if (Configuration.extendedCreativeHotbar && player.isCreative()) {
			for (int slot = 0; slot < 4; ++slot) {
				int i1;
				int j1 = sr.getScaledHeight() - 16 - 3;
				int stableSlot = player.getPrimaryHand().opposite() == EnumHandSide.RIGHT ? 8 : 0;
				if (handSide == EnumHandSide.LEFT) {
					i1 = i + 90 + slot * 20 + 3;
				} else {
					i1 = i - 90 - 80 + slot * 20;
				}
				if (CutelessMod.hotbarSlot >= 9 && CutelessMod.hotbarSlot % 9 == slot) {
					this.renderHotbarItem(i1, j1, partialTicks, player, player.inventory.mainInventory.get(stableSlot));
				} else {
					this.renderHotbarItem(i1, j1, partialTicks, player, player.inventory.mainInventory.get(27 + slot));
				}
			}
		}
	}
}
