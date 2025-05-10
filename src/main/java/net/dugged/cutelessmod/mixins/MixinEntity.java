package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(Entity.class)
public abstract class MixinEntity {
	@Shadow
	public World world;
	@Shadow
	public double posX;
	@Shadow
	public double posY;
	@Shadow
	public double posZ;
	@Shadow
	public float rotationPitch;
	@Shadow
	public double motionX;
	@Shadow
	public double motionY;
	@Shadow
	public double motionZ;

	@Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onEntityWalk(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)V", shift = At.Shift.AFTER))
	private void addParticles(MoverType d0, double l4, double d12, double i5, CallbackInfo ci) {
		if (!Configuration.showSnowDripParticles) {
			return;
		}
		if (world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY), MathHelper.floor(this.posZ))).getBlock() instanceof BlockSnow) {
			Random rand = new Random();
			if (rand.nextInt(5) == 0) {
				world.spawnParticle(EnumParticleTypes.END_ROD, this.posX + rand.nextFloat() - 0.5F, this.posY + 0.1D, this.posZ + rand.nextFloat() - 0.5F, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	/**
	 * Fixes snapaim inaccuracy when snapping to different angles.
	 *
	 * @author X-com (Xcom6000)
	 */
	@Inject(method = "moveRelative", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;sin(F)F"), cancellable = true)
	public void cutelessmod$snapFix(final float strafe, final float up, final float forward, final float friction, final CallbackInfo ci) {
		if ((Entity) (Object) this instanceof EntityPlayerSP) {
			float f1 = cutelessmod$round((float) Math.sin(this.rotationPitch * 0.01745329251994329576923690768489D));
			float f2 = cutelessmod$round((float) Math.cos(this.rotationPitch * 0.01745329251994329576923690768489D));
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
