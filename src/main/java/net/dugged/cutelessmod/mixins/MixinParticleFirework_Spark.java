package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ParticleFirework.Spark.class)
public abstract class MixinParticleFirework_Spark extends Particle {
	protected MixinParticleFirework_Spark(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@Unique
	private static final Random cutelessmodRNG = new Random();

	@Inject(method = "<init>", at = @At("RETURN"))
	private void rainbowFireworksTrail(World world, double x, double y, double z, double dx, double dy, double dz, ParticleManager manager, CallbackInfo ci) {
		if (Configuration.colouredFireworksTrail) {
			this.particleRed = cutelessmodRNG.nextFloat();
			this.particleGreen = cutelessmodRNG.nextFloat();
			this.particleBlue = cutelessmodRNG.nextFloat();
		}
	}
}
