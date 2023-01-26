package net.dugged.cutelessmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

// Stolen from https://github.com/TeamDeusVult/MagnesiumExtras
public enum Zoomer {
	INSTANCE;

	public static final GameSettings options = Minecraft.getMinecraft().gameSettings;
	private final float defaultLevel = 3F;
	private float currentLevel = defaultLevel;
	private Float defaultMouseSensitivity;

	public float changeFovBasedOnZoom(final float fov) {
		final float mouseSensitivitySetting = options.mouseSensitivity;
		if (!CutelessMod.zoomerKey.isKeyDown()) {
			this.currentLevel = defaultLevel;
			if (this.defaultMouseSensitivity != null) {
				options.mouseSensitivity = this.defaultMouseSensitivity;
				this.defaultMouseSensitivity = null;
			}

			return fov;
		}

		if (this.defaultMouseSensitivity == null) {
			this.defaultMouseSensitivity = mouseSensitivitySetting;
		}

		// Adjust mouse sensitivity in relation to zoom level.
		// 1.0 / currentLevel is a value between 0.02 (50x zoom)
		// and 1 (no zoom).
		options.mouseSensitivity = this.defaultMouseSensitivity * (1F / this.currentLevel);
		return fov / this.currentLevel;
	}

	public void onMouseScroll() {
		if (!CutelessMod.zoomerKey.isKeyDown()) {
			return;
		}

		final int amount = Mouse.getEventDWheel();
		if (amount > 0) {
			this.currentLevel *= 1.1F;
		} else if (amount < 0) {
			this.currentLevel *= 0.9F;
		}

		this.currentLevel = MathHelper.clamp(this.currentLevel, 1F, 50F);
	}
}
