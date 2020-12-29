package net.dugged.cutelessmod.mixins;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.storage.WorldSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.List;

@Mixin(GuiWorldSelection.class)
public abstract class MixinGuiWorldSelection extends GuiScreen {

	@Shadow
	private GuiListWorldSelection selectionList;
	private GuiTextField searchField;

	@Inject(method = "initGui", at = @At("HEAD"))
	private void addSearchField(CallbackInfo ci) {
		searchField = new GuiTextField(0, fontRenderer, fontRenderer.getStringWidth(new TextComponentTranslation("text.cutelessmod.search").getUnformattedText()) + 24, 8, width / 4, 16);
		searchField.setMaxStringLength(32500);
		searchField.setFocused(true);
	}

	@Inject(method = "drawScreen", at = @At("RETURN"))
	private void drawScreen(int mouseX, int mouseY, float partialTicks, final CallbackInfo ci) {
		drawString(fontRenderer, new TextComponentTranslation("text.cutelessmod.search").getUnformattedText(), 16, 12, 16777215);
		searchField.drawTextBox();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		searchField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		if (searchField.textboxKeyTyped(typedChar, keyCode)) {
			((IGuiListWorldSelection) selectionList).setEntries(Lists.newArrayList());
			selectionList.refreshList();
			if (!searchField.getText().isEmpty()) {
				List<GuiListWorldSelectionEntry> entries = Lists.newArrayList();
				for (GuiListWorldSelectionEntry entry : ((IGuiListWorldSelection) selectionList).getEntries()) {
					WorldSummary summary = ((IGuiListWorldSelectionEntry) entry).getWorldSummary();
					if (summary.getFileName().toLowerCase().contains(searchField.getText().toLowerCase()) || summary.getDisplayName().toLowerCase().contains(searchField.getText().toLowerCase())) {
						entries.add(entry);
					}
				}
				((IGuiListWorldSelection) selectionList).setEntries(entries);
			}
		}
	}
}
