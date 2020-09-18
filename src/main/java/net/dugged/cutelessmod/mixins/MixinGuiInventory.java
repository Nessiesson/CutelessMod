package net.dugged.cutelessmod.mixins;

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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiInventory.class)
public abstract class MixinGuiInventory extends InventoryEffectRenderer
{
    @Shadow private GuiButtonImage recipeButton;

    @Shadow @Final private GuiRecipeBook recipeBookGui;
    private static final ResourceLocation POTION_BUTTON = new ResourceLocation("textures/potion_button.png");
    private boolean showPotionEffects = false;

    public MixinGuiInventory(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Inject(method = "drawScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/inventory/GuiInventory;recipeBookGui:Lnet/minecraft/client/gui/recipebook/GuiRecipeBook;", ordinal = 1))
    private void drawScreen(CallbackInfo ci) {
        this.hasActivePotionEffects = !this.recipeBookGui.isVisible() && showPotionEffects;
        if(GuiScreen.isShiftKeyDown() && recipeButton.isMouseOver()) {
            ((IGuiButtonImage)recipeButton).setResourceLocation(POTION_BUTTON);
            ((IGuiButtonImage)recipeButton).setXTexStart(0);
            ((IGuiButtonImage)recipeButton).setYDiffText(0);
        } else {
            ((IGuiButtonImage)recipeButton).setResourceLocation(INVENTORY_BACKGROUND);
            ((IGuiButtonImage)recipeButton).setXTexStart(178);
            ((IGuiButtonImage)recipeButton).setYDiffText(19);
        }
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    private void actionPerformed(GuiButton button, CallbackInfo ci) {
        if(GuiScreen.isShiftKeyDown() && recipeButton.isMouseOver() && button.id == 10 && !this.recipeBookGui.isVisible()) {
            showPotionEffects = !showPotionEffects;
            ci.cancel();
        }
    }

}
