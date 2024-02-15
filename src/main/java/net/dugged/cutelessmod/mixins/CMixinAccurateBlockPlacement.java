package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockGlazedTerracotta;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// Modified and slightly cleaned up version of Xcom's Carpet Client accurateBlockPlacement code.
// https://github.com/X-com/CarpetClient/blob/3153ef9dedf99f770c9688a8545ef354c0c5108a/src/main/java/carpetclient/mixins/MixinPlayerControllerMP.java
public abstract class CMixinAccurateBlockPlacement {
	@Mixin(PlayerControllerMP.class)
	public abstract static class MixinPlayerControllerMP {
		@ModifyArg(method = "processRightClickBlock", index = 3, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/client/CPacketPlayerTryUseItemOnBlock;<init>(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/util/EnumHand;FFF)V"))
		public float sendPacketReplace(final BlockPos pos, final EnumFacing face, final EnumHand hand, final float fX, final float fY, final float fZ) {
			return Configuration.carpetAccurateBlockPlacement ? cutelessmod$encodeBlockRotation(pos, face, hand, fX) : fX;
		}

		/**
		 * A rotation algorithm that will sneek data in the unused x face value. Data will be decoded by carpet
		 * mod "accurateBlockPlacement" and place the block in the orientation that is coded.
		 *
		 * @param pos   Position of the Block being placed
		 * @param dir   The direction of the block being placed into. Rather the facing side the player is clicking on.
		 * @param hand  The hand currently trying to right-click the block.
		 * @param fX    The old X value that is used unused currently in vanilla minecraft. Y value is used to place blocks on the top of bottom part (stairs/slabs).
		 * @return the encoded value for the specific orientation that is determined.
		 */
		@Unique
		private float cutelessmod$encodeBlockRotation(final BlockPos pos, final EnumFacing dir, final EnumHand hand, final float fX) {
			final EntityPlayerSP player = Minecraft.getMinecraft().player;
			final Block item = Block.getBlockFromItem(player.getHeldItem(hand).getItem());
			if (!cutelessmod$shouldRotate(fX, item)) {
				return fX;
			}

			EnumFacing face = dir;
			if (CutelessMod.carpetFaceIntoKey.isKeyDown() && uselessmod$isPiston(item)) {
				face = face.getOpposite();
			} else {
				if (uselessmod$isRedstoneDiode(item) || uselessmod$isGlazedTerracotta(item)) {
					face = player.getHorizontalFacing().getOpposite();
				} else {
					face = EnumFacing.getDirectionFromEntityLiving(pos.offset(face), player);
				}

				if (uselessmod$isObserver(item)) {
					face = face.getOpposite();
				}
			}

			if (CutelessMod.carpetFlipFaceKey.isKeyDown()) {
				face = face.getOpposite();
			}

			return 2F + face.getIndex();
		}

		/**
		 * Checks for the item types that should be accurate placed, skips everything else.
		 * If f value is above 1 then the protocol is already being used and also returns false to skip rotation.
		 */
		@Unique
		private boolean cutelessmod$shouldRotate(final float f, final Block block) {
			if (f > 1) {
				return false;
			}

			return uselessmod$isDispenser(block) || uselessmod$isGlazedTerracotta(block) || uselessmod$isObserver(block) || uselessmod$isPiston(block) || uselessmod$isRedstoneDiode(block);
		}

		@Unique
		private boolean uselessmod$isDispenser(final Block block) {
			return block instanceof BlockDispenser;
		}

		@Unique
		private boolean uselessmod$isGlazedTerracotta(final Block block) {
			return block instanceof BlockGlazedTerracotta;
		}

		@Unique
		public boolean uselessmod$isObserver(final Block block) {
			return block instanceof BlockObserver;
		}

		@Unique
		private boolean uselessmod$isPiston(final Block block) {
			return block instanceof BlockPistonBase;
		}

		@Unique
		private boolean uselessmod$isRedstoneDiode(final Block block) {
			return block instanceof BlockRedstoneDiode;
		}
	}
}
