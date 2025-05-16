package net.dugged.cutelessmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class DespawnSphereRenderer implements IStationaryRenderer {
    private static final DespawnSphereRenderer instance = new DespawnSphereRenderer();

    private static final double RADIUS = 128.0;
    private static final int SLICES = 360;
    private static final int STACKS = 180;

    private int lastCx = Integer.MIN_VALUE;
    private int lastCz = Integer.MIN_VALUE;
    private int displayListId = -1;

    private static boolean visible = false;
    private static double x, y, z;

    public static DespawnSphereRenderer getInstance() {
        return instance;
    }

    @Override
    public void updatePosition(EntityPlayer player) {
        double px = player.posX;
        double py = player.posY;
        double pz = player.posZ;

        int cx = MathHelper.floor(px);
        int cz = MathHelper.floor(pz);

        if (cx == lastCx && cz == lastCz) {
            visible =! visible;
        } else {
            x = px;
            y = py;
            z = pz;
            visible = true;
            lastCx = cx;
            lastCz = cz;
            rebuildDisplayList();
        }
    }

    @Override
    public void render(float partialTicks) {
        if (!visible || displayListId == -1) {
            return;
        }

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        double d1 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        CutelessModUtils.drawString(partialTicks, "Despawn Sphere Renderer", (float) x, (float) (y + 0.4f), (float) z, 0);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x - d1, y - d2, z - d3);

        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.glLineWidth(2.0F);
        AxisAlignedBB bb = new AxisAlignedBB(x - 0.01, y, z - 0.01, x + 0.01, y + 0.4,
                z + 0.01).offset(-d1, -d2, -d3);
        RenderGlobal.drawSelectionBoundingBox(bb, 1.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glCallList(displayListId);
        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private void rebuildDisplayList() {
        if (displayListId != -1) {
            GL11.glDeleteLists(displayListId, 1);
        }
        displayListId = GL11.glGenLists(1);
        GL11.glNewList(displayListId, GL11.GL_COMPILE);
        GL11.glBegin(GL11.GL_LINES);
        for (int i = 0; i <= STACKS; i++) {
            double phi = Math.PI * i / STACKS;
            double yOff = RADIUS * Math.cos(phi);
            double r = RADIUS * Math.sin(phi);
            for (int j = 0; j < SLICES; j++) {
                double theta1 = 2 * Math.PI * j / SLICES;
                double theta2 = 2 * Math.PI * (j + 1) / SLICES;
                double x1 = r * Math.cos(theta1);
                double z1 = r * Math.sin(theta1);
                double x2 = r * Math.cos(theta2);
                double z2 = r * Math.sin(theta2);
                GL11.glVertex3d(x1, yOff, z1);
                GL11.glVertex3d(x2, yOff, z2);
            }
        }

        for (int j = 0; j < SLICES; j++) {
            double theta = 2 * Math.PI * j / SLICES;
            double cosT = Math.cos(theta);
            double sinT = Math.sin(theta);
            for (int i = 0; i < STACKS; i++) {
                double phi1 = Math.PI * i / STACKS;
                double phi2 = Math.PI * (i + 1) / STACKS;
                double x1 = RADIUS * Math.sin(phi1) * cosT;
                double y1 = RADIUS * Math.cos(phi1);
                double z1 = RADIUS * Math.sin(phi1) * sinT;
                double x2 = RADIUS * Math.sin(phi2) * cosT;
                double y2 = RADIUS * Math.cos(phi2);
                double z2 = RADIUS * Math.sin(phi2) * sinT;
                GL11.glVertex3d(x1, y1, z1);
                GL11.glVertex3d(x2, y2, z2);
            }
        }
        GL11.glEnd();
        GL11.glEndList();
    }
}