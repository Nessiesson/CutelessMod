package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumDyeColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerListEntryNormal.class)
public abstract class MixinServerListEntryNormal {
	@Shadow
	@Final
	private ServerData server;

	@Shadow
	@Final
	private GuiMultiplayer owner;

	@Redirect(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I", ordinal = 0))
	private int hideServerNames(final FontRenderer fontRenderer, final String text, final int x, final int y, final int color) {
		return Configuration.showServerNames ? fontRenderer.drawString(text, x, y, color) : 0;
	}

	@Inject(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/FMLClientHandler;enhanceServerListEntry(Lnet/minecraft/client/gui/ServerListEntryNormal;Lnet/minecraft/client/multiplayer/ServerData;IIIII)Ljava/lang/String;", remap = false))
	private void alwaysShowPing(final int slotIndex, final int x, final int y, final int listWidth, final int slotHeight, final int mouseX, final int mouseY, final boolean isSelected, final float partialTicks, final CallbackInfo ci) {
		if (!Configuration.alwaysShowPing || this.server.pingToServer < 0) {
			return;
		}

		final int dX = x + listWidth + (((IGuiMultiplayer) this.owner).getServerListSelector().getMaxScroll() > 0 ? 8 : 0);
		Minecraft.getMinecraft().fontRenderer.drawString(this.server.pingToServer + "ms", dX, y, EnumDyeColor.SILVER.getColorValue());
		GlStateManager.color(1F, 1F, 1F, 1F);
	}
}