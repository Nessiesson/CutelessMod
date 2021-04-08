package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Reference;
import net.minecraft.client.Minecraft;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiInventory.class)
public abstract class MixinGuiInventory extends InventoryEffectRenderer {
	@Unique
	private static final ResourceLocation CUTELESSMOD_POTION_BUTTON = new ResourceLocation(Reference.MODID, "textures/potion_button.png");
	@Unique
	private boolean cutelessModShowPotionEffects = false;
	@Shadow
	private GuiButtonImage recipeButton;
	@Shadow
	@Final
	private GuiRecipeBook recipeBookGui;

	public MixinGuiInventory(final Container container) {
		super(container);
	}

	@Inject(method = "drawScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/inventory/GuiInventory;recipeBookGui:Lnet/minecraft/client/gui/recipebook/GuiRecipeBook;", ordinal = 1))
	private void drawScreen(final CallbackInfo ci) {
		this.hasActivePotionEffects = !this.recipeBookGui.isVisible() && cutelessModShowPotionEffects;
		// Cast directly instead of creating var to prevent crash, see https://github.com/SpongePowered/Mixin/issues/305
		if (GuiScreen.isShiftKeyDown() && this.recipeButton.isMouseOver()) {
			((IGuiButtonImage) this.recipeButton).setResourceLocation(CUTELESSMOD_POTION_BUTTON);
			((IGuiButtonImage) this.recipeButton).setXTexStart(0);
			((IGuiButtonImage) this.recipeButton).setYDiffText(0);
		} else {
			((IGuiButtonImage) this.recipeButton).setResourceLocation(INVENTORY_BACKGROUND);
			((IGuiButtonImage) this.recipeButton).setXTexStart(178);
			((IGuiButtonImage) this.recipeButton).setYDiffText(19);
		}
	}

	@Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
	private void actionPerformed(final GuiButton button, final CallbackInfo ci) {
		if (GuiScreen.isShiftKeyDown() && this.recipeButton.isMouseOver() && button.id == 10 && !this.recipeBookGui.isVisible()) {
			this.cutelessModShowPotionEffects = !this.cutelessModShowPotionEffects;
			ci.cancel();
		}
	}

	@ModifyVariable(method = "drawEntityOnScreen(IIIFFLnet/minecraft/entity/EntityLivingBase;)V", ordinal = 1, index = 1, name = "posY", at = @At("HEAD"), argsOnly = true)
	private static int centreElytraFlyingPlayer(int value, final int posX, final int posY, final int scale) {
		if (Minecraft.getMinecraft().player.isElytraFlying()) {
			if (scale == 30) {
				value -= 25;
			} else if (scale == 20) {
				value -= 16;
			}
		}

		return value;
	}
}
