package net.dugged.cutelessmod.clientcommands.mixins;

import net.dugged.cutelessmod.clientcommands.TaskRenderer;
import net.dugged.cutelessmod.clientcommands.worldedit.WorldEditRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

	@Inject(method = "renderWorldPass", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=litParticles"))
	private void onPostRenderEntities(final int pass, final float partialTicks,
		final long finishTimeNano, final CallbackInfo ci) {
		WorldEditRenderer.render(partialTicks);
		TaskRenderer.renderTasksFancy(partialTicks);
	}
}
