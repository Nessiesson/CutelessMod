package net.dugged.cutelessmod;

import net.dugged.cutelessmod.mixins.IMapItemRenderer;
import net.dugged.cutelessmod.mixins.IMapItemRendererInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;

public class MapDisplay {
	private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
	private static final float scale = 0.5F;

	public static void handleMapDisplayRenderer(ItemStack stack, int x, int y, Gui gui) {
		if (stack != null && stack.getItem() instanceof ItemMap && GuiScreen.isShiftKeyDown()) {
			Minecraft mc = Minecraft.getMinecraft();
			MapData mapdata = Items.FILLED_MAP.getMapData(stack, mc.world);
			if (mapdata != null) {
				IMapItemRenderer renderer = (IMapItemRenderer) mc.entityRenderer.getMapItemRenderer();
				MapItemRenderer.Instance instance = renderer.getInstance(mapdata);
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.translate(x - (142.0D * scale), y - (142.0D * scale), 100.0F);
				GlStateManager.scale(scale, scale, scale);
				handleTexture(renderer.getTextureManager(), RES_MAP_BACKGROUND, 0.0D, 142.0D, 800.0D);
				handleTexture(renderer.getTextureManager(), ((IMapItemRendererInstance) instance).getLocation(), 7.0D, 128.0D, 800.0D);
				GlStateManager.enableLighting();
				GlStateManager.scale(1.0F, 1.0F, 1.0F);
				GlStateManager.popMatrix();
			}
		}
	}

	private static void handleTexture(TextureManager textureManager, ResourceLocation texture, double offset, double size, double zLevel) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		textureManager.bindTexture(texture);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(offset, offset + size, zLevel).tex(0.0D, 1.0D).endVertex();
		bufferbuilder.pos(offset + size, offset + size, zLevel).tex(1.0D, 1.0D).endVertex();
		bufferbuilder.pos(offset + size, offset, zLevel).tex(1.0D, 0.0D).endVertex();
		bufferbuilder.pos(offset, offset, zLevel).tex(0.0D, 0.0D).endVertex();
		tessellator.draw();
	}
}
