package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {
	@Unique
	private double cutelessmodD0;
	@Unique
	private double cutelessmodD1;
	@Unique
	private double cutelessmodD2;

	public MixinEntityLivingBase(World world) {
		super(world);
	}

	@Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;setPosition(DDD)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void keepCopies(final CallbackInfo ci, final double d0, final double d1, final double d2, final double d3) {
		this.cutelessmodD0 = d0;
		this.cutelessmodD1 = d1;
		this.cutelessmodD2 = d2;
	}

	@Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;setPosition(DDD)V"))
	private void fixSquidAndWitherMovement(final EntityLivingBase entity, final double x, final double y, final double z) {
		if (entity instanceof EntityWither) {
			entity.move(MoverType.SELF, this.cutelessmodD0 - this.posX, this.cutelessmodD1 - this.posY, this.cutelessmodD2 - this.posZ);
		} else {
			entity.setPosition(x, y, z);
		}
	}

	@Redirect(method = "onDeathUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnParticle(Lnet/minecraft/util/EnumParticleTypes;DDDDDD[I)V"))
	private void cutelessmod$onSummonDeathParticles(final World instance, final EnumParticleTypes particleType, final double xCoord, final double yCoord, final double zCoord, final double xSpeed, final double ySpeed, final double zSpeed, final int[] parameters) {
		if (Configuration.showDeathParticles) {
			instance.spawnParticle(particleType, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
		}
	}

	/**
	 * Fixes snapaim inaccuracy when snapping to different angles.
	 *
	 * @author X-com (Xcom6000)
	 */
	@Inject(method = "moveRelative", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;sin(F)F"), cancellable = true)
	public void cutelessmod$snapFix(final float strafe, final float up, final float forward, final float friction, final CallbackInfo ci) {
		if ((EntityLivingBase) (Object) this instanceof EntityPlayerSP) {
			float f1 = cutelessmod$round((float) Math.sin(this.rotationYaw * 0.01745329251994329576923690768489D));
			float f2 = cutelessmod$round((float) Math.cos(this.rotationYaw * 0.01745329251994329576923690768489D));
			this.motionX += strafe * f2 - forward * f1;
			this.motionY += up;
			this.motionZ += forward * f2 + strafe * f1;
			ci.cancel();
		}
	}

	@Unique
	private float cutelessmod$round(float value) {
		final long factor = 100_000_000L;
		return (float) ((long) (value * factor)) / factor;
	}
}
