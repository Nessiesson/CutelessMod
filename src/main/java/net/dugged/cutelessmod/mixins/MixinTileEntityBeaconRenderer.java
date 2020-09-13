package net.dugged.cutelessmod.mixins;


import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.block.BlockBeacon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(TileEntityBeaconRenderer.class)
public abstract class MixinTileEntityBeaconRenderer {

    private boolean hasNeighbourBeacons(final BlockPos pos, final World world, final int checkX, int checkZ) {
        for (BlockPos posToCheck : BlockPos.MutableBlockPos.getAllInBox(pos, pos.add(checkX, 0, checkZ))) {
            if (!posToCheck.equals(pos) && world.getBlockState(posToCheck).getBlock() instanceof BlockBeacon && ((TileEntityBeacon) world.getTileEntity(posToCheck)).getLevels() > 0) {
                return true;
            }
        }
        return false;
    }

    private AxisAlignedBB getBeaconBlockBB (final BlockPos pos, final World world, final EntityPlayer player, final float partialTicks) {
        final double d1 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        final double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        final double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        int x = 0;
        int z = 0;
        while (hasNeighbourBeacons(pos.add(x, 0, 0), world, 1, 0)) {
            x++;
        }
        while (hasNeighbourBeacons(pos.add(0, 0, z), world, 0, 1)) {
            z++;
        }
        return new AxisAlignedBB(pos).offset(-d1, -d2, -d3).expand(x, 0, z);
    }

    private AxisAlignedBB getBeaconAreaBB (TileEntityBeacon te, AxisAlignedBB beaconBlockBB) {
        return beaconBlockBB.grow(te.getLevels() * 10 + 10).expand(0.0, te.getWorld().getHeight(), 0.0);

    }

    private AxisAlignedBB getNextBeaconBB (TileEntityBeacon te, AxisAlignedBB beaconBlockBB, final int offsetX, final int offsetZ) {
        return beaconBlockBB.expand(0.0, -beaconBlockBB.minY, 0.0).expand(0.0, te.getWorld().getHeight(), 0.0).offset(offsetX, 0.0, offsetZ);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render(TileEntityBeacon te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo ci) {
        if (CutelessMod.toggleBeaconArea > 0 && ((IEntityBeacon) te).getIsComplete() && te.getLevels() > 0) {
            final BlockPos pos = te.getPos();
            final World world = te.getWorld();
             if (!hasNeighbourBeacons(pos, world, -1, -1)) {
                 final EntityPlayer player = Minecraft.getMinecraft().player;
                 final int scale = 256;
                 final int colour = pos.getX() + scale * pos.getZ() + scale * 256 * pos.getY();
                 final float[] colours = new Color((int) (colour * (16777215F / (256F * scale)))).getRGBColorComponents(null);
                 final AxisAlignedBB beaconBlockBB = getBeaconBlockBB(pos, world, player, partialTicks);
                 final AxisAlignedBB beaconAreaBB = getBeaconAreaBB(te, beaconBlockBB);
                 final int offset = (te.getLevels() * 10 + 10) * 2;
                 System.out.println(offset);
                 GlStateManager.depthMask(false);
                 GlStateManager.disableFog();
                 GlStateManager.disableLighting();
                 GlStateManager.disableTexture2D();
                 if (CutelessMod.toggleBeaconArea == 2) {
                     RenderGlobal.renderFilledBox(beaconAreaBB, colours[0], colours[1], colours[2], 0.15F);
                 }
                 if (CutelessMod.toggleBeaconArea == 3) {
                     for (int x1 = -offset; x1 < offset * 2; x1 += offset) {
                         for (int z1 = -offset; z1 < offset * 2; z1 += offset) {
                             RenderGlobal.drawSelectionBoundingBox(getNextBeaconBB(te, beaconBlockBB, x1, z1), colours[0], colours[1], colours[2], 1F);
                         }
                     }
                 }
                 RenderGlobal.renderFilledBox(beaconBlockBB, colours[0], colours[1], colours[2], 0.3F);
                 RenderGlobal.drawSelectionBoundingBox(beaconAreaBB, colours[0], colours[1], colours[2], 1F);
                 GlStateManager.enableTexture2D();
                 GlStateManager.enableLighting();
                 GlStateManager.enableFog();
                 GlStateManager.depthMask(true);
             }
        }
    }
}