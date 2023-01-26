package net.dugged.cutelessmod.mixins;

import com.google.common.collect.Multimap;
import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.translation.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import javax.annotation.Nullable;
import java.util.Comparator;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
	//TODO: Raw type
	private final static Comparator comp = Comparator.<NBTTagCompound>comparingInt(t -> t.getShort("lvl"))
			.reversed()
			.thenComparingInt(t -> t.getShort("id"));

	@Shadow
	public abstract NBTTagList getEnchantmentTagList();

	@Shadow
	@Nullable
	public abstract NBTTagCompound getTagCompound();

	@Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multimap;isEmpty()Z", remap = false))
	private boolean noAttributes(final Multimap<String, AttributeModifier> map) {
		return !Configuration.showItemAttributes || map.isEmpty() || !Minecraft.getMinecraft().gameSettings.advancedItemTooltips;
	}

	@Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getEnchantmentTagList()Lnet/minecraft/nbt/NBTTagList;"))
	private NBTTagList sortEnchantments(final ItemStack item) {
		final NBTTagList list = this.getEnchantmentTagList();
		if (Configuration.sortEnchantmentTooltip) {
			((INBTTagList) list).getTagList().sort(comp);
		}

		return list;
	}

	@Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/text/translation/I18n;translateToLocalFormatted(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", ordinal = 0), slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=item.nbt_tags")))
	private String showNBT(final String key, final Object... format) {
		if (!GuiScreen.isAltKeyDown()) {
			return I18n.translateToLocalFormatted(key, format);
		}

		return this.getTagCompound().toString();
	}
}
