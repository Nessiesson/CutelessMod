package net.dugged.cutelessmod;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

// Modified version of the shulkerbox display from Vazkii's Quark Forgemod.
public class ShulkerBoxDisplay {
	private static final ResourceLocation WIDGET_RESOURCE = new ResourceLocation("cutelessmod", "textures/shulker_widget.png");

	public static void handleShulkerBoxDisplayRenderer(ItemStack stack, int x, int y, Gui gui) {
		if (stack != null && stack.getItem() instanceof ItemShulkerBox && stack.hasTagCompound() && GuiScreen.isShiftKeyDown()) {
			final NBTTagCompound cmp = getCompoundOrNull(stack);
			if (cmp != null && cmp.hasKey("Items", 9)) {
				final int texWidth = 172;
				final int texHeight = 64;
				final int currentY = y - texHeight - 18;

				GlStateManager.pushMatrix();
				RenderHelper.enableGUIStandardItemLighting();
				GlStateManager.enableRescaleNormal();
				GlStateManager.translate(0F, 0F, 700F);

				final Minecraft mc = Minecraft.getMinecraft();
				mc.getTextureManager().bindTexture(WIDGET_RESOURCE);

				final EnumDyeColor dye = ((BlockShulkerBox) ((ItemBlock) stack.getItem()).getBlock()).getColor();
				final float[] colours = dye.getColorComponentValues();
				GlStateManager.color(colours[0], colours[1], colours[2]);

				gui.drawTexturedModalRect(x, currentY, 0, 0, texWidth, texHeight);

				final NonNullList<ItemStack> itemList = NonNullList.withSize(27, ItemStack.EMPTY);
				ItemStackHelper.loadAllItems(cmp, itemList);

				final RenderItem render = mc.getRenderItem();

				GlStateManager.enableDepth();
				int i = 0;
				for (ItemStack itemstack : itemList) {
					if (!itemstack.isEmpty()) {
						final int xp = x + 6 + (i % 9) * 18;
						final int yp = currentY + 6 + (i / 9) * 18;

						render.renderItemAndEffectIntoGUI(itemstack, xp, yp);
						render.renderItemOverlays(mc.fontRenderer, itemstack, xp, yp);
					}

					i++;
				}

				GlStateManager.disableDepth();
				GlStateManager.popMatrix();
			}
		}
	}

	private static NBTTagCompound getCompoundOrNull(ItemStack stack) {
		final NBTTagCompound compound = stack.getTagCompound();
		return compound != null && compound.hasKey("BlockEntityTag") ? compound.getCompoundTag("BlockEntityTag") : null;
	}
}
