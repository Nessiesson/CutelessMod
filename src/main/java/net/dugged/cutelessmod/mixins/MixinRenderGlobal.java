package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal {
	private static final Map<BlockPos, Entity> entityMap = new HashMap<>();
	private static final Map<BlockPos, Integer> counterMap = new HashMap<>();
	@Shadow
	@Final
	private Minecraft mc;
	@Shadow
	@Final
	private RenderManager renderManager;

	@Inject(method = "notifyLightSet", at = @At("HEAD"), cancellable = true)
	private void noLight(BlockPos pos, CallbackInfo ci) {
		if (!Configuration.lightUpdates) {
			ci.cancel();
		}
	}

	@Inject(method = "isOutlineActive", at = @At("HEAD"), cancellable = true)
	private void highlightAllEntitites(Entity entityIn, Entity viewer, ICamera camera, CallbackInfoReturnable<Boolean> cir) {
		if (CutelessMod.highlightEntities) {
			cir.setReturnValue((entityIn instanceof EntityLivingBase || entityIn instanceof EntityMinecart) && (entityIn.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(entityIn.getEntityBoundingBox()) || entityIn.isRidingOrBeingRiddenBy(this.mc.player)));
		}
	}

	@Inject(method = "renderEntities", at = @At("HEAD"))
	private void resetCounts(Entity renderViewEntity, ICamera camera, float partialTicks, CallbackInfo ci) {
		entityMap.clear();
		counterMap.clear();
	}

	@Redirect(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;shouldRender(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;DDD)Z"))
	private boolean renderStackedEntities(RenderManager renderManager, Entity entityIn, ICamera camera, double camX, double camY, double camZ) {
		if (Configuration.stackedEntities && !entityIn.isDead) {
			BlockPos pos = entityIn.getPosition();
			if (entityMap.containsKey(pos)) {
				counterMap.put(pos, counterMap.get(pos) + 1);
				return false;
			} else {
				entityMap.put(pos, entityIn);
				counterMap.put(pos, 1);
				return true;
			}
		} else {
			return renderManager.shouldRender(entityIn, camera, camX, camY, camZ);
		}
	}

	@Inject(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;isRenderEntityOutlines()Z", shift = At.Shift.BEFORE))
	private void renderCounts(Entity renderViewEntity, ICamera camera, float partialTicks, CallbackInfo ci) {
		if (Configuration.stackedEntities) {
			for (BlockPos pos : entityMap.keySet()) {
				Entity entity = entityMap.get(pos);
				int count = counterMap.get(pos);
				if (count > 1 && entity.getDistanceSq(this.renderManager.renderViewEntity) <= 16384) {
					double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
					double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
					double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
					x -= ((IRenderManager) this.renderManager).getRenderPosX();
					y -= ((IRenderManager) this.renderManager).getRenderPosY();
					z -= ((IRenderManager) this.renderManager).getRenderPosZ();
					boolean flag = entity.isSneaking();
					float f = this.renderManager.playerViewY;
					float f1 = this.renderManager.playerViewX;
					boolean flag1 = this.renderManager.options.thirdPersonView == 2;
					float f2 = entity.height + 0.5F - (flag ? 0.25F : 0.0F);
					String s = mc.gameSettings.showDebugInfo ? EntityList.getEntityString(entity) + ": " + count : String.valueOf(count);
					EntityRenderer.drawNameplate(this.renderManager.getFontRenderer(), s, (float) x, (float) y + f2, (float) z, 0, f, f1, flag1, flag);
				}
			}
		}
	}
}
