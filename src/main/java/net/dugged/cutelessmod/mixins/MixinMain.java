package net.dugged.cutelessmod.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Main.class)
public class MixinMain {
	// Just for testing to always receive game instance with Player0
	private static final boolean DEBUG_MODE = false;

	@Redirect(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getSystemTime()J"))
	private static long debugMode() {
		if (DEBUG_MODE) {
			return 1000;
		} else {
			return Minecraft.getSystemTime();
		}
	}
}
