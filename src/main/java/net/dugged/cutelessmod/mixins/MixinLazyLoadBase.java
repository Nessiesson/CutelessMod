package net.dugged.cutelessmod.mixins;

import net.minecraft.util.LazyLoadBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LazyLoadBase.class)
public abstract class MixinLazyLoadBase<T> {
	@Shadow
	private boolean isLoaded;
	@Shadow
	private T value;

	@Shadow
	protected abstract T load();

	/**
	 * @author LX_Gaming
	 * @reason Make Thread-safe (Fix for MC-68381)
	 */
	@Overwrite
	public T getValue() {
		synchronized (this) {
			if (!this.isLoaded) {
				this.isLoaded = true;
				this.value = this.load();
			}

			return this.value;
		}
	}
}
