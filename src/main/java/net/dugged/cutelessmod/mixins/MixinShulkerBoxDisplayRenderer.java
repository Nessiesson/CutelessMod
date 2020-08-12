package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.ShulkerBoxDisplay;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {GuiScreen.class, GuiContainerCreative.class})
public abstract class MixinShulkerBoxDisplayRenderer extends Gui {
	@Inject(method = "renderToolTip", at = @At("RETURN"))
	private void postRenderToolTip(ItemStack stack, int x, int y, CallbackInfo ci) {
		if (Configuration.showShulkerBoxDisplay) {
			ShulkerBoxDisplay.handleShulkerBoxDisplayRenderer(stack, x, y, this);
		}
	}
}