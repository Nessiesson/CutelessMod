package net.dugged.cleancuteless;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

// Loosely based on Zume https://github.com/Nolij/Zume
public enum Zoomer {
	INSTANCE;

	private static final double MAX_ZOOM = 50D;
	private static final double SCROLL_MULTIPLIER = 1.1D;
	private static final float MIN_FOV = 1F;
	private static final Minecraft mc = Minecraft.getMinecraft();

	private double currentZoom = 1D;
	private double targetZoom = 1D;
	private double animationStartZoom = 1D;
	private long animationStart;
	private boolean active;

	public void update() {
		final boolean held = mc.currentScreen == null && KeyBindings.zoomerKey.isKeyDown();
		if (held != this.active) {
			this.active = held;
			this.targetZoom = held ? MathHelper.clamp(Configuration.visuals.zoomAmount, 1D, MAX_ZOOM) : 1D;
			this.startAnimation();
		}

		this.advanceAnimation();
	}

	public boolean onMouseScroll(final int delta) {
		if (delta == 0 || !this.active || !Configuration.visuals.zoomScroll || !KeyBindings.zoomerKey.isKeyDown() || mc.currentScreen != null) {
			return false;
		}

		final double multiplier = delta > 0 ? SCROLL_MULTIPLIER : 1D / SCROLL_MULTIPLIER;
		this.targetZoom = MathHelper.clamp(this.targetZoom * multiplier, 1D, MAX_ZOOM);
		this.startAnimation();
		return true;
	}

	public float changeFovBasedOnZoom(final float fov) {
		return Math.max(MIN_FOV, (float) (fov / this.currentZoom));
	}

	public float adjustMouseSensitivity(final float sensitivity) {
		if (this.currentZoom <= 1D) {
			return sensitivity;
		}

		final double floor = MathHelper.clamp(Configuration.visuals.zoomSensitivityFloor, 0D, 1D);
		// Matches Zume's gentler sensitivity curve, which keeps large zoom levels controllable.
		final double zoomFactor = Math.sqrt(1D / this.currentZoom);
		return (float) (sensitivity * (floor + (1D - floor) * zoomFactor));
	}

	public boolean adjustSmoothCamera(final boolean smoothCamera) {
		return smoothCamera || (Configuration.visuals.zoomCinematic && this.currentZoom > 1.0001D);
	}

	private void startAnimation() {
		final int duration = Configuration.visuals.zoomSmoothnessMs;
		if (duration <= 0) {
			this.currentZoom = this.targetZoom;
			return;
		}

		this.animationStartZoom = this.currentZoom;
		this.animationStart = System.currentTimeMillis();
	}

	private void advanceAnimation() {
		final int duration = Configuration.visuals.zoomSmoothnessMs;
		if (duration <= 0) {
			this.currentZoom = this.targetZoom;
			return;
		}

		final double progress = MathHelper.clamp((System.currentTimeMillis() - this.animationStart) / (double) duration, 0D, 1D);
		final double exponent = Math.max(1D, Configuration.visuals.zoomAnimationEasing);
		// Zume-style ease-out: most of the movement happens early, then settles smoothly.
		final double eased = 1D - Math.pow(1D - progress, exponent);
		this.currentZoom = Math.exp(MathHelper.clampedLerp(Math.log(this.animationStartZoom), Math.log(this.targetZoom), eased));
		if (progress >= 1D) {
			this.currentZoom = this.targetZoom;
		}
	}
}
