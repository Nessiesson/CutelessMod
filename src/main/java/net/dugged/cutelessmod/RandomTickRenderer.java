package net.dugged.cutelessmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

public class RandomTickRenderer implements IStationaryRenderer {
    private static final RandomTickRenderer instance = new RandomTickRenderer();
    private static final int RANGE = 128;
    private static final int RANGE_SQ = RANGE * RANGE;

    private int lastCx = Integer.MIN_VALUE;
    private int lastCz = Integer.MIN_VALUE;
    private int displayListId = -1;

    private static boolean visible = false;
    private static double x, y, z;

    public static RandomTickRenderer getInstance() {
        return instance;
    }

    @Override
    public void updatePosition(EntityPlayer player) {
        int cx = MathHelper.floor(player.posX);
        int cz = MathHelper.floor(player.posZ);

        if (cx == lastCx && cz == lastCz) {
            visible =! visible;
        } else {
            x = player.posX;
            y = player.posY;
            z = player.posZ;
            visible = true;

            lastCx = cx;
            lastCz = cz;

            rebuildDisplayList();
        }
    }

    @Override
    public void render(float partialTicks) {
        if (!visible || displayListId == -1) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        double d1 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        CutelessModUtils.drawString(partialTicks, "Random Tick Renderer", (float) x, (float) (y + 0.4f), (float) z, 0);
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
        GlStateManager.glLineWidth(1.0F);
        GlStateManager.popMatrix();
    }

    private void rebuildDisplayList() {
        if (displayListId != -1) {
            GL11.glDeleteLists(displayListId, 1);
        }
        displayListId = GL11.glGenLists(1);
        GL11.glNewList(displayListId, GL11.GL_COMPILE);
        GL11.glBegin(GL11.GL_LINES);

        int centerCx = MathHelper.floor(x);
        int centerCz = MathHelper.floor(z);

        Set<Coord> inRange = new HashSet<>();
        for (int cx = centerCx - RANGE; cx <= centerCx + RANGE; cx++) {
            for (int cz = centerCz - RANGE; cz <= centerCz + RANGE; cz++) {
                double dx = (cx + 0.5 - x);
                double dz = (cz + 0.5 - z);
                if (dx * dx + dz * dz <= RANGE_SQ) {
                    inRange.add(new Coord(cx >> 4, cz >> 4));
                }
            }
        }

        double bottomY = -y;
        double topY = 255 - y;

        for (Coord c : inRange) {
            int chunkX = c.x;
            int chunkZ = c.z;

            boolean north = !inRange.contains(new Coord(chunkX,     chunkZ - 1));
            boolean south = !inRange.contains(new Coord(chunkX,     chunkZ + 1));
            boolean west  = !inRange.contains(new Coord(chunkX - 1, chunkZ));
            boolean east  = !inRange.contains(new Coord(chunkX + 1, chunkZ));

            double minX = (chunkX << 4) - x;
            double maxX = ((chunkX + 1) << 4) - x;
            double minZ = (chunkZ << 4) - z;
            double maxZ = ((chunkZ + 1) << 4) - z;

            // Draw only outer faces
            if (north) {
                drawLine(minX, bottomY, minZ, maxX, bottomY, minZ);
                drawLine(minX, topY,    minZ, maxX, topY,    minZ);
                drawLine(minX, bottomY, minZ, minX, topY,    minZ);
                drawLine(maxX, bottomY, minZ, maxX, topY,    minZ);
            }
            if (south) {
                drawLine(minX, bottomY, maxZ, maxX, bottomY, maxZ);
                drawLine(minX, topY,    maxZ, maxX, topY,    maxZ);
                drawLine(minX, bottomY, maxZ, minX, topY,    maxZ);
                drawLine(maxX, bottomY, maxZ, maxX, topY,    maxZ);
            }
            if (west) {
                drawLine(minX, bottomY, minZ, minX, bottomY, maxZ);
                drawLine(minX, topY,    minZ, minX, topY,    maxZ);
                drawLine(minX, bottomY, minZ, minX, topY,    minZ);
                drawLine(minX, bottomY, maxZ, minX, topY,    maxZ);
            }
            if (east) {
                drawLine(maxX, bottomY, minZ, maxX, bottomY, maxZ);
                drawLine(maxX, topY,    minZ, maxX, topY,    maxZ);
                drawLine(maxX, bottomY, minZ, maxX, topY,    minZ);
                drawLine(maxX, bottomY, maxZ, maxX, topY,    maxZ);
            }
        }

        GL11.glEnd();
        GL11.glEndList();
    }

    private static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2) {
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x2, y2, z2);
    }

    private static class Coord {
        final int x, z;
        Coord(int x, int z) { this.x = x; this.z = z; }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Coord)) return false;
            Coord c = (Coord) o;
            return x == c.x && z == c.z;
        }
        @Override public int hashCode() {
            return 31 * x + z;
        }
    }
}