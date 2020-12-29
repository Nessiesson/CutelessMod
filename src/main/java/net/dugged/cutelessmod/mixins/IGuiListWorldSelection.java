package net.dugged.cutelessmod.mixins;

import net.minecraft.client.gui.GuiListWorldSelection;
import net.minecraft.client.gui.GuiListWorldSelectionEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(GuiListWorldSelection.class)
public interface IGuiListWorldSelection {
	@Accessor
	List<GuiListWorldSelectionEntry> getEntries();

	@Mutable
	@Accessor
	void setEntries(List<GuiListWorldSelectionEntry> entries);
}
