package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityEnderman.class)
public abstract class MixinEntityEnderman extends EntityMob {
	public MixinEntityEnderman(final World world) {
		super(world);
	}

	@Inject(method = "onLivingUpdate", at = @At("RETURN"))
	private void derpyEnderman(final CallbackInfo ci) {
		if (Configuration.derpyChicken) {
			this.rotationPitch = -180F;
		}
	}
}
