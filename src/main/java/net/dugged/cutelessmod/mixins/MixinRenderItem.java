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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//TODO: redo to have proper textures show up everywhere.
//TODO: see ItemMeshDefinition
@Mixin(RenderItem.class)
public abstract class MixinRenderItem {
	@Unique
	private boolean cutelessmod$isPerfectBasicToolBase(final ItemStack stack) {
		return EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) >= 3 && EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) > 0;
	}

	@Unique
	private boolean cutelessmod$isPerfectToolBase(final ItemStack stack) {
		return this.cutelessmod$isPerfectBasicToolBase(stack) && EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack) >= 5;
	}

	@Unique
	private boolean cutelessmod$isPerfectSilk(final ItemStack stack) {
		return this.cutelessmod$isPerfectToolBase(stack) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0;
	}

	@Unique
	private boolean cutelessmod$isPerfectFortune(final ItemStack stack) {
		return this.cutelessmod$isPerfectToolBase(stack) && EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack) >= 3;
	}

	@Unique
	private boolean cutelessmod$isPerfectSilkAxe(final ItemStack stack) {
		return this.cutelessmod$isPerfectSilk(stack) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) >= 5;
	}

	@Unique
	private boolean cutelessmod$isPerfectFortuneAxe(final ItemStack stack) {
		return this.cutelessmod$isPerfectFortune(stack) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) >= 5;
	}

	@Unique
	private boolean cutelessmod$isPerfectNetherPick(final ItemStack stack) {
		return EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack) == 3
				&& EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) >= 3
				&& EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0;
	}

	@Unique
	private boolean cutelessmod$isPerfectSword(final ItemStack stack) {
		return this.cutelessmod$isPerfectBasicToolBase(stack)
				&& EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) >= 5
				&& EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, stack) >= 3
				&& EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING, stack) >= 3
				&& EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack) >= 2;
	}

	@Unique
	private boolean cutelessmod$isEmptyShulkerBox(final ItemStack stack) {
		final NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			return true;
		}

		return !tag.getCompoundTag("BlockEntityTag").hasKey("Items");
	}

	@Inject(method = "renderItemOverlayIntoGUI", at = @At("RETURN"))
	private void postRenderItemCount(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text, CallbackInfo ci) {
		if (!Configuration.showIdealToolMarker || stack.isEmpty()) {
			return;
		}

		final Item item = stack.getItem();
		String marker = "";
		if (item instanceof ItemAxe) {
			if (this.cutelessmod$isPerfectSilkAxe(stack)) {
				marker = "S";
			} else if (this.cutelessmod$isPerfectFortuneAxe(stack)) {
				marker = "F";
			}
		} else if (item instanceof ItemElytra && this.cutelessmod$isPerfectBasicToolBase(stack)) {
			marker = "P";
		} else if (item instanceof ItemFirework && stack.hasTagCompound()) {
			final NBTTagCompound itemData = stack.getTagCompound();
			if (itemData != null) {
				final NBTTagCompound fireworks = itemData.getCompoundTag("Fireworks");
				marker = String.valueOf(fireworks.getByte("Flight"));
			}
		} else if (item instanceof ItemFlintAndSteel && this.cutelessmod$isPerfectBasicToolBase(stack)) {
			marker = "P";
		} else if (item instanceof ItemHoe && this.cutelessmod$isPerfectBasicToolBase(stack)) {
			marker = "P";
		} else if (item instanceof ItemPickaxe) {
			if (this.cutelessmod$isPerfectSilk(stack)) {
				marker = "S";
			} else if (this.cutelessmod$isPerfectFortune(stack)) {
				marker = "F";
			} else if (this.cutelessmod$isPerfectNetherPick(stack)) {
				marker = "N";
			}
		} else if (item instanceof ItemShears && this.cutelessmod$isPerfectToolBase(stack)) {
			marker = "P";
		} else if (item instanceof ItemSpade) {
			if (this.cutelessmod$isPerfectSilk(stack)) {
				marker = "S";
			} else if (this.cutelessmod$isPerfectFortune(stack)) {
				marker = "F";
			}
		} else if (item instanceof ItemSword && this.cutelessmod$isPerfectSword(stack)) {
			marker = "P";
		} else if (item instanceof ItemFirework) {
			final NBTTagCompound compound = stack.getSubCompound("Fireworks");
			if (compound != null && compound.hasKey("Flight", 99)) {
				marker = String.valueOf(compound.getByte("Flight"));
			}
		} else if (item instanceof ItemShulkerBox && this.cutelessmod$isEmptyShulkerBox(stack)) {
			marker = "E";
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
