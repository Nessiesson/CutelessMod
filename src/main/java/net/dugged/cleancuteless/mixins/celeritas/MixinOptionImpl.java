package net.dugged.cleancuteless.mixins.celeritas;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

/**
 * Applies CleanCuteless' fullbright gamma handling to Celeritas' own brightness slider.
 * Celeritas writes gammaSetting directly instead of going through GameSettings#setOptionFloatValue.
 */
@SuppressWarnings("public-target")
@Mixin(targets = "org.taumc.celeritas.api.options.structure.OptionImpl", remap = false)
public abstract class MixinOptionImpl {
	@Unique
	private static Field cleancuteless$idField;

	@Unique
	private boolean cleancuteless$brightnessChecked;

	@Unique
	private boolean cleancuteless$brightnessOption;

	@Shadow
	private Object modifiedValue;

	@Inject(method = "getValue", at = @At("HEAD"), cancellable = true)
	private void cleancuteless$showFullbrightValue(final CallbackInfoReturnable<Object> cir) {
		if (this.modifiedValue == null && this.cleancuteless$isBrightnessOption() && Minecraft.getMinecraft().gameSettings.gammaSetting > 1F) {
			cir.setReturnValue(100);
		}
	}

	@Inject(method = "applyChanges", at = @At("RETURN"))
	private void cleancuteless$applyFullbright(final CallbackInfo ci) {
		if (!this.cleancuteless$isBrightnessOption()) {
			return;
		}

		final GameSettings settings = Minecraft.getMinecraft().gameSettings;
		if (settings.gammaSetting >= 0.95F) {
			settings.gammaSetting = 1000F;
		}
	}

	@Unique
	private boolean cleancuteless$isBrightnessOption() {
		if (this.cleancuteless$brightnessChecked) {
			return this.cleancuteless$brightnessOption;
		}

		this.cleancuteless$brightnessChecked = true;

		try {
			if (cleancuteless$idField == null) {
				cleancuteless$idField = this.getClass().getDeclaredField("id");
				cleancuteless$idField.setAccessible(true);
			}

			final Object id = cleancuteless$idField.get(this);
			if (id != null) {
				final Class<?> idClass = id.getClass();
				final Object modId = idClass.getMethod("getModId").invoke(id);
				final Object path = idClass.getMethod("getPath").invoke(id);
				this.cleancuteless$brightnessOption = "minecraft".equals(modId) && "brightness".equals(path);
			}
		} catch (final ReflectiveOperationException ignored) {
		}

		return this.cleancuteless$brightnessOption;
	}
}
