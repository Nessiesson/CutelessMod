package net.dugged.cutelessmod.mixins;

import com.google.common.collect.Multimap;
import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Comparator;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
	@Shadow
	public abstract NBTTagList getEnchantmentTagList();

	//TODO: Raw type
	private final static Comparator comp = Comparator.<NBTTagCompound>comparingInt(t -> t.getShort("lvl"))
			.reversed()
			.thenComparingInt(t -> t.getShort("id"));

	@Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multimap;isEmpty()Z", remap = false))
	private boolean noAttributes(Multimap<String, AttributeModifier> map) {
		return !Configuration.showItemAttributes || map.isEmpty() || !Minecraft.getMinecraft().gameSettings.advancedItemTooltips;
	}

	@Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getEnchantmentTagList()Lnet/minecraft/nbt/NBTTagList;"))
	private NBTTagList sortEnchantments(ItemStack item) {
		final NBTTagList list = this.getEnchantmentTagList();
		if (Configuration.sortEnchantmentTooltip) {
			((INBTTagList) list).getTagList().sort(comp);
		}

		return list;
	}
}
