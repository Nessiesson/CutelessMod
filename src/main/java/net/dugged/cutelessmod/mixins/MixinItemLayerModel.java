package net.dugged.cutelessmod.mixins;

import com.google.common.collect.ImmutableList;
import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.model.TRSRTransformation;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@SuppressWarnings({"DiscouragedInjectionPoint", "InvalidInjectorMethodSignature", "OptionalUsedAsFieldOrParameterType", "PointlessArithmeticExpression"})
@Mixin(ItemLayerModel.class)
public abstract class MixinItemLayerModel {
	@Shadow
	private static BakedQuad buildQuad(final VertexFormat format, final Optional<TRSRTransformation> transform, final EnumFacing side, final TextureAtlasSprite sprite, final int tint, final float x0, final float y0, final float z0, final float u0, final float v0, final float x1, final float y1, final float z1, final float u1, final float v1, final float x2, final float y2, final float z2, final float u2, final float v2, final float x3, final float y3, final float z3, final float u3, final float v3) {
		throw new UnsupportedOperationException();
	}

	@Inject(method = "getQuadsForSprite", at = @At(value = "JUMP", opcode = Opcodes.GOTO, ordinal = 0), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/util/EnumFacing;DOWN:Lnet/minecraft/util/EnumFacing;", opcode = Opcodes.GETSTATIC, ordinal = 0)), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void cutelessmod$fewerDots(final int tint, final TextureAtlasSprite sprite, final VertexFormat format, final Optional<TRSRTransformation> transform, final CallbackInfoReturnable<ImmutableList<BakedQuad>> cir,
	                                          final ImmutableList.Builder<BakedQuad> builder, final int width, final int height, final @Coerce Object faceData, final boolean translucent, final int f, final int[] pixels, final boolean ptu, final boolean[] ptv, final int v, int u, final int alpha, final boolean t) {
		if (t || !Configuration.zzzSlightlyBetterItemStitchingMaybeKindaNotReally) {
			return;
		}

		// we inject after a ++u so we have to correct for that.
		--u;

		// some smol number, chosen at random (trial/error idek)
		final float eps = 1F / 8192;

		final float uMin = (1 - eps) * (u + 0F) / width;
		final float uMax = (1 + eps) * (u + 1F) / width;
		final float vMin = (1 - eps) * (v + 0F) / height;
		final float vMax = (1 + eps) * (v + 1F) / height;

		final float uScale = 16F / width;
		final float vScale = 16F / height;

		final float uMini = sprite.getInterpolatedU(uScale * (u + 0));
		final float uMaxi = sprite.getInterpolatedU(uScale * (u + 1));
		final float vMini = sprite.getInterpolatedV(vScale * (height - v));
		final float vMaxi = sprite.getInterpolatedV(vScale * (height - v - 1));

		// front
		builder.add(buildQuad(format, transform, EnumFacing.NORTH, sprite, tint,
				uMin, vMin, 7.5F / 16F, uMini, vMaxi,
				uMin, vMax, 7.5F / 16F, uMini, vMini,
				uMax, vMax, 7.5F / 16F, uMaxi, vMini,
				uMax, vMin, 7.5F / 16F, uMaxi, vMaxi
		));

		// back
		builder.add(buildQuad(format, transform, EnumFacing.SOUTH, sprite, tint,
				uMin, vMin, 8.5F / 16F, uMini, vMaxi,
				uMax, vMin, 8.5F / 16F, uMaxi, vMaxi,
				uMax, vMax, 8.5F / 16F, uMaxi, vMini,
				uMin, vMax, 8.5F / 16F, uMini, vMini
		));
	}



	@Inject(method = "getQuadsForSprite", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/model/ItemLayerModel;buildQuad(Lnet/minecraft/client/renderer/vertex/VertexFormat;Ljava/util/Optional;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;IFFFFFFFFFFFFFFFFFFFF)Lnet/minecraft/client/renderer/block/model/BakedQuad;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private static void cutelessmod$doNotDrawTheseWhenStitching(final int tint, final TextureAtlasSprite sprite, final VertexFormat format, final Optional<TRSRTransformation> transform, final CallbackInfoReturnable<ImmutableList<BakedQuad>> cir,
	                                                            final ImmutableList.Builder<BakedQuad> builder) {
		if (Configuration.zzzSlightlyBetterItemStitchingMaybeKindaNotReally) {
			cir.setReturnValue(builder.build());
		}
	}
}
