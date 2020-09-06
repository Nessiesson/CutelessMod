package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.resources.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(GuiStats.class)
public class MixinGuiStats extends GuiScreen {

    private GuiTextField textField;
    private long lastTick = 0;
    private int cooldown = 0;
    private static final Logger LOGGER = LogManager.getLogger();


    @Inject(method = "initGui", at = @At("HEAD"))
    public void initGui(CallbackInfo ci) {
        textField = new GuiTextField(0, this.fontRenderer, this.width / 2 - 4 - 150, this.height - 28, 150, 20);
        textField.setText(CutelessMod.statPluginFilter);
        textField.setMaxStringLength(32500);
        textField.setFocused(true);
    }

    @Inject(method = "initButtons", at = @At("HEAD"))
    private void addButons(final CallbackInfo ci) {
        if (CutelessMod.statPlugin.isConnected()) {
            this.buttonList.add(new GuiButton(5, this.width / 2 - 154 - 86, this.height - 28, 80, 20, I18n.format("text.cutelessmod.disconnect")));
        } else {
            this.buttonList.add(new GuiButton(5, this.width / 2 - 154 - 86, this.height - 28, 80, 20, I18n.format("text.cutelessmod.connect")));
        }
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    private void handleButtons(GuiButton button, final CallbackInfo ci) {
        if (button.id == 5 && cooldown <= 0) {
            try
            {
                if (CutelessMod.statPlugin.isConnected()) {
                    CutelessMod.statPlugin.disconnect();
                    button.displayString = I18n.format("text.cutelessmod.connect");
                } else {
                    CutelessMod.statPlugin.connect();
                    button.displayString = I18n.format("text.cutelessmod.disconnect");
                }
            }
            catch (Exception exception)
            {
                LOGGER.warn("Unable to execute StatPlugin: {}", (Object)exception.getMessage());
            }
            cooldown = 10;
            ci.cancel();
        }
        if (button.id == 0) {
            CutelessMod.statPluginFilter = textField.getText();
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void drawScreen (int mouseX, int mouseY, float partialTicks, final CallbackInfo ci) {
        textField.drawTextBox();
        if(cooldown > 0 && CutelessMod.tickCounter > lastTick) {
            lastTick = CutelessMod.tickCounter;
            cooldown--;
        }
        for (GuiButton button : buttonList) {
            if (button.id == 5) {
                if (cooldown > 0) {
                    button.enabled = false;
                } else {
                    button.enabled = true;
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        textField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        textField.textboxKeyTyped(typedChar, keyCode);
    }
}
