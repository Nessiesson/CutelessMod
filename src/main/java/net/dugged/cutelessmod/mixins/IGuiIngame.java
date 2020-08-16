package net.dugged.cutelessmod.mixins;

import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiIngame.class)
public interface IGuiIngame {
    @Accessor
    String getOverlayMessage();
    @Accessor
    int getOverlayMessageTime();
}
