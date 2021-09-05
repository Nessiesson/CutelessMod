package net.dugged.cutelessmod.mixins.optifine;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(targets = "net/optifine/override/PlayerControllerOF", remap = false)
public interface IPlayerControllerOF {
	@Dynamic
	@Accessor(value = "acting", remap = false)
	void setActing(final boolean value);
}
