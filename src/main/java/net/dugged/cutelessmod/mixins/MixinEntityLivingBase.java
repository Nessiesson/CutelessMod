package net.dugged.cutelessmod.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.boss.EntityWither;
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
	private void keepCopies(final CallbackInfo ci, final double d0, final double d1, final double d2) {
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
}