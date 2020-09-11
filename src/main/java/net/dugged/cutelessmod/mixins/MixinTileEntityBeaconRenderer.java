package net.dugged.cutelessmod.mixins;


import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(TileEntityBeaconRenderer.class)
public abstract class MixinTileEntityBeaconRenderer {
    @Inject(method = "render", at = @At("RETURN"))
    private void render(TileEntityBeacon te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo ci) {
        if (!CutelessMod.toggleBeaconArea) {
            return;
        }

        if (((IEntityBeacon) te).getIsComplete() && te.getLevels() > 0) {
            final EntityPlayer player = Minecraft.getMinecraft().player;
            final double d0 = te.getLevels() * 10 + 10;
            final double d1 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
            final double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
            final double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
            final BlockPos pos = te.getPos();
            // It's not good, but it seems reasonably random.
            final int colour = pos.getX() + 101 * pos.getZ() + 41942 * pos.getY();
            //final int colour = (pos.getX() % 1024) * (pos.getZ() % 1024) * 16;
            final float[] colours = new Color((int) (colour * (16777215.0 / 2611456.0))).getRGBColorComponents(null);
            //final float[] colours = new Color(colour).getRGBColorComponents(null);
            final AxisAlignedBB axisalignedbb = (new AxisAlignedBB(pos)).offset(-d1, -d2, -d3).grow(d0).expand(0.0, te.getWorld().getHeight(), 0.0);
            GlStateManager.depthMask(false);
            GlStateManager.disableFog();
            GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();
            RenderGlobal.renderFilledBox(axisalignedbb, colours[0], colours[1], colours[2], 0.2F);
            RenderGlobal.drawSelectionBoundingBox(axisalignedbb, colours[0], colours[1], colours[2], 1F);
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableFog();
            GlStateManager.depthMask(true);
        }
    }
}