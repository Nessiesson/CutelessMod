package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRecipeBook.class)
public abstract class MixinGuiRecipeBook {
    @Inject(method = "updateStackedContents", at = @At("RETURN"))
    private void craftingHax(CallbackInfo ci) {
        if (!Configuration.craftingHax) {
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();
        final PlayerControllerMP controller = mc.playerController;
        final EntityPlayerSP player = mc.player;
        final int id = player.openContainer.windowId;
        if (GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown()) {
            controller.windowClick(id, 0, 1, GuiScreen.isAltKeyDown() ? ClickType.THROW : ClickType.QUICK_MOVE, player);
        }
    }
}