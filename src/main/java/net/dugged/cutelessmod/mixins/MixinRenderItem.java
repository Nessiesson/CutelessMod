package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//TODO: redo to have proper textures show up everywhere.
//TODO: see ItemMeshDefinition
@Mixin(RenderItem.class)
public abstract class MixinRenderItem {
	private boolean isPerfectBasicToolBase(ItemStack stack) {
		return EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) >= 3 && EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) > 0;
	}

	private boolean isPerfectToolBase(ItemStack stack) {
		return this.isPerfectBasicToolBase(stack) && EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack) >= 5;
	}

	private boolean isPerfectSilk(ItemStack stack) {
		return this.isPerfectToolBase(stack) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0;
	}

	private boolean isPerfectFortune(ItemStack stack) {
		return this.isPerfectToolBase(stack) && EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack) >= 3;
	}

	private boolean isPerfectSilkAxe(ItemStack stack) {
		return this.isPerfectSilk(stack) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) >= 5;
	}

	private boolean isPerfectFortuneAxe(ItemStack stack) {
		return this.isPerfectFortune(stack) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) >= 5;
	}

	private boolean isPerfectNetherPick(ItemStack stack) {
		return EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack) == 3
				&& EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) >= 3
				&& EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0;
	}

	private boolean isPerfectSword(ItemStack stack) {
		return this.isPerfectBasicToolBase(stack)
				&& EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) >= 5
				&& EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, stack) >= 3
				&& EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING, stack) >= 3
				&& EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack) >= 2;
	}

	@Inject(method = "renderItemOverlayIntoGUI", at = @At("RETURN"))
	private void postRenderItemCount(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text, CallbackInfo ci) {
		if (!Configuration.showIdealToolMarker || stack.isEmpty()) {
			return;
		}

		final Item item = stack.getItem();
		String marker = "";
		if (item instanceof ItemAxe) {
			if (this.isPerfectSilkAxe(stack)) {
				marker = "S";
			} else if (this.isPerfectFortuneAxe(stack)) {
				marker = "F";
			}
		} else if (item instanceof ItemElytra && this.isPerfectBasicToolBase(stack)) {
			marker = "P";
		} else if (item instanceof ItemFirework && stack.hasTagCompound()) {
			final NBTTagCompound itemData = stack.getTagCompound();
			if (itemData != null) {
				final NBTTagCompound fireworks = itemData.getCompoundTag("Fireworks");
				marker = String.valueOf(fireworks.getByte("Flight"));
			}
		} else if (item instanceof ItemFlintAndSteel && this.isPerfectBasicToolBase(stack)) {
			marker = "P";
		} else if (item instanceof ItemHoe && this.isPerfectBasicToolBase(stack)) {
			marker = "P";
		} else if (item instanceof ItemPickaxe) {
			if (this.isPerfectSilk(stack)) {
				marker = "S";
			} else if (this.isPerfectFortune(stack)) {
				marker = "F";
			} else if (this.isPerfectNetherPick(stack)) {
				marker = "N";
			}
		} else if (item instanceof ItemShears && this.isPerfectToolBase(stack)) {
			marker = "P";
		} else if (item instanceof ItemSpade) {
			if (this.isPerfectSilk(stack)) {
				marker = "S";
			} else if (this.isPerfectFortune(stack)) {
				marker = "F";
			}
		} else if (item instanceof ItemSword && this.isPerfectSword(stack)) {
			marker = "P";
		} else if (item instanceof ItemFirework) {
			final NBTTagCompound compound = stack.getSubCompound("Fireworks");
			if (compound != null && compound.hasKey("Flight", 99)) {
				marker = String.valueOf(compound.getByte("Flight"));
			}
		}

		if (!marker.equals("")) {
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			GlStateManager.disableBlend();
			GlStateManager.pushMatrix();
			final float f = 0.5F;
			GlStateManager.scale(f, f, f);
			fr.drawStringWithShadow(marker, (xPosition) / f, (yPosition + 12) / f, 0xFFFFFF);
			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			GlStateManager.enableBlend();
		}
	}
}
