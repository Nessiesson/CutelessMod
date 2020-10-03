package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Reference;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiInventory.class)
public abstract class MixinGuiInventory extends InventoryEffectRenderer {
	@Shadow
	private GuiButtonImage recipeButton;
	@Shadow
	@Final
	private GuiRecipeBook recipeBookGui;
	@Unique
	private static final ResourceLocation CUTELESSMOD_POTION_BUTTON = new ResourceLocation(Reference.MODID, "textures/potion_button.png");
	@Unique
	private boolean cutelessModShowPotionEffects = false;

	public MixinGuiInventory(final Container container) {
		super(container);
	}

	@Inject(method = "drawScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/inventory/GuiInventory;recipeBookGui:Lnet/minecraft/client/gui/recipebook/GuiRecipeBook;", ordinal = 1))
	private void drawScreen(final CallbackInfo ci) {
		this.hasActivePotionEffects = !this.recipeBookGui.isVisible() && cutelessModShowPotionEffects;
		final IGuiButtonImage button = (IGuiButtonImage) this.recipeButton;
		if (GuiScreen.isShiftKeyDown() && this.recipeButton.isMouseOver()) {
			button.setResourceLocation(CUTELESSMOD_POTION_BUTTON);
			button.setXTexStart(0);
			button.setYDiffText(0);
		} else {
			button.setResourceLocation(INVENTORY_BACKGROUND);
			button.setXTexStart(178);
			button.setYDiffText(19);
		}
	}

	@Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
	private void actionPerformed(final GuiButton button, final CallbackInfo ci) {
		if (GuiScreen.isShiftKeyDown() && this.recipeButton.isMouseOver() && button.id == 10 && !this.recipeBookGui.isVisible()) {
			this.cutelessModShowPotionEffects = !this.cutelessModShowPotionEffects;
			ci.cancel();
		}
	}
}
