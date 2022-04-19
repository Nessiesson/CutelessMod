package net.dugged.cutelessmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;

public class ItemCounter {
	private static final Map<ItemStack, Long> itemList = new HashMap<>();
	private static final List<Integer> itemIds = new ArrayList<>();
	public static BlockPos position = null;

	public static void renderPos(float partialTicks) {
		CutelessModUtils.drawCube(partialTicks, position, 1.0F, 0.8F, 0.0F);
	}

	public static void renderGui(float partialTicks) {
		if (position == null) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderHelper.enableGUIStandardItemLighting();
		Map<ItemStack, Long> sortedMap = itemList.entrySet().stream().sorted(Map.Entry.<ItemStack, Long>comparingByValue().reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		int count = 0;
		for (ItemStack stack : sortedMap.keySet()) {
			if (8 + ((count + 1) * 4 + 16 * (count + 1)) > sr.getScaledHeight()) {
				break;
			}
			int yOffset = mc.player.getActivePotionEffects().isEmpty() ? 0 : 16;
			String s = sortedMap.get(stack).toString();
			float f = (float) stack.getAnimationsToGo() - partialTicks;
			int x = sr.getScaledWidth() - 24;
			int y = yOffset + 8 + (count * 4 + 16 * count);
			if (f > 0.0F) {
				GlStateManager.pushMatrix();
				float f1 = 1.0F + f / 5.0F;
				GlStateManager.translate((float) (x + 8), (float) (y + 12), 0.0F);
				GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
				GlStateManager.translate((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
			}
			RenderItem itemRenderer = mc.getRenderItem();
			itemRenderer.renderItemAndEffectIntoGUI(mc.player, stack, x, y);
			if (f > 0.0F) {
				GlStateManager.popMatrix();
			}
			mc.fontRenderer.drawString(s, sr.getScaledWidth() - 24 - 4 - mc.fontRenderer.getStringWidth(s), yOffset + 17 - (mc.fontRenderer.FONT_HEIGHT / 2) + (count * 4 + 16 * count), 16777215);
			count++;
		}
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
	}

	public static void checkItem(int entityId, ItemStack stack) {
		if (!itemIds.contains(entityId) && stack.getMaxStackSize() > 1) {
			boolean contained = false;
			for (ItemStack stack1 : itemList.keySet()) {
				if (stack1.getItem().equals(stack.getItem()) && (!stack1.getItem().getHasSubtypes() || (stack1.getItem().getHasSubtypes() && stack.getMetadata() == stack1.getMetadata()))) {
					contained = true;
					itemList.put(stack1, itemList.get(stack1) + stack.getCount());
				}
			}
			if (!contained) {
				ItemStack stack1 = new ItemStack(stack.getItem(), stack.getCount(), stack.getItemDamage());
				itemList.put(stack1, (long) stack1.getCount());
			}
			itemIds.add(entityId);
		}
	}

	public static boolean checkPosition(int x, int y, int z) {
		if (position == null) {
			return false;
		}
		if (x < 0) {
			x--;
		}
		if (z < 0) {
			z--;
		}
		return x == position.getX() && y == position.getY() && z == position.getZ();
	}

	public static void reset() {
		itemList.clear();
		itemIds.clear();
	}
}
