package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleDigging.class)
public abstract class MixinParticleDigging extends Particle {
	@Shadow
	@Final
	private IBlockState sourceState;

	protected MixinParticleDigging(World worldIn, double posXIn, double posYIn, double posZIn) {
		super(worldIn, posXIn, posYIn, posZIn);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void removeRandomParticleMotion(CallbackInfo ci) {
		final double multiplier = Configuration.blockBreakingMultiplier;
		if (Math.abs(multiplier - 1D) >= 1E-1D) {
			this.motionX *= multiplier;
			this.motionY *= multiplier;
			this.motionZ *= multiplier;
			this.particleMaxAge *= 1 + multiplier * 2D / 3D;
			this.particleGravity = 0F;
		}

		if (Configuration.fixBlock36Particles && this.sourceState.getBlock() == Blocks.PISTON_EXTENSION) {
			final TileEntity te = this.world.getTileEntity(new BlockPos(this.posX, this.posY, this.posZ));
			final BlockModelShapes bms = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
			if (te instanceof TileEntityPiston) {
				final TileEntityPiston piston = (TileEntityPiston) te;
				this.setParticleTexture(bms.getTexture(piston.getPistonState()));
			} else {
				this.setParticleTexture(bms.getTexture(Blocks.STONE.getDefaultState()));
			}
		}
	}
}
