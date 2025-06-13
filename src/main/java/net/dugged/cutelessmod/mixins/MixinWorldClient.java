package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldClient.class, priority = 1001)
public abstract class MixinWorldClient extends World {
	protected MixinWorldClient(final ISaveHandler ish, final WorldInfo wi, final WorldProvider wp, final Profiler p, final boolean c) {
		super(ish, wi, wp, p, c);
	}

	@Override
	public void updateEntity(final Entity entity) {
		if (Configuration.clientEntityUpdates || entity instanceof EntityPlayer || entity instanceof EntityFireworkRocket) {
			super.updateEntity(entity);
		}
	}

	@Override
	public float getRainStrength(final float delta) {
		return Configuration.showRain ? this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * delta : 0F;
	}

	@Inject(method = "playMoodSoundAndCheckLight", at = @At("HEAD"), cancellable = true)
	private void cutelessmod$disableMoodSoundAndCheckLight(final int x, final int z, final Chunk chunkIn, final CallbackInfo ci) {
		if (Configuration.disableMoodSoundAndCheckLight) {
			ci.cancel();
		}
	}
}
