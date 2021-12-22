package net.dugged.cutelessmod.clientcommands.worldedit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

public class WorldEditRenderer {
	public static CopyOnWriteArrayList<RenderedBB> bbToRender = new CopyOnWriteArrayList<>();
	public static void render(float partialTicks) {
		final EntityPlayerSP player = Minecraft.getMinecraft().player;
		final double d1 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		final double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		final double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		GlStateManager.glLineWidth(3F);
		for (Map.Entry<WorldEditSelection.SelectionType, WorldEditSelection> entry : WorldEdit.selections.entrySet()) {
			WorldEditSelection.SelectionType type = entry.getKey();
			WorldEditSelection selection = entry.getValue();
			if (selection.getPos(A) != null | selection.getPos(B) != null) {
				GlStateManager.depthFunc(GL11.GL_ALWAYS);
				if (selection.getPos(A) != null) {
					AxisAlignedBB posBB = new AxisAlignedBB(selection.getPos(A)).offset(-d1, -d2, -d3);
					RenderGlobal.drawSelectionBoundingBox(posBB, A.getColor().getRed() / 255.0F, A.getColor().getGreen() / 255.0F, A.getColor().getBlue() / 255.0F, 0.6F);
				}
				if (selection.getPos(B) != null) {
					AxisAlignedBB posBB = new AxisAlignedBB(selection.getPos(B)).offset(-d1, -d2, -d3);
					RenderGlobal.drawSelectionBoundingBox(posBB, B.getColor().getRed() / 255.0F, B.getColor().getGreen() / 255.0F, B.getColor().getBlue() / 255.0F, 0.6F);
				}
				GlStateManager.depthFunc(GL11.GL_LEQUAL);
				if (selection.isCompleted()) {
					AxisAlignedBB posBB = selection.getBB().offset(-d1, -d2, -d3).expand(1, 1, 1).grow(0.0075F);
					RenderGlobal.drawSelectionBoundingBox(posBB, type.getColor().getRed() / 255.0F, type.getColor().getGreen() / 255.0F, type.getColor().getBlue() / 255.0F, 0.6F);
				}
			}
		}
		// TODO: More visualizations
		for (RenderedBB bb : bbToRender) {
			RenderGlobal.drawSelectionBoundingBox(bb.offset(-d1, -d2, -d3), 1F, 0.0F, 0.0F, 1.0F);
		}
		GlStateManager.glLineWidth(1F);
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.enableBlend();
	}

	public static void update() {
		bbToRender.removeIf(pos -> pos.getTimer() <= 0);
		for (RenderedBB bb : bbToRender) {
			bb.decreaseTimer();
		}
	}

	static class RenderedBB extends AxisAlignedBB {
		private int timer;
		public RenderedBB(BlockPos pos1, BlockPos pos2, int timer) {
			super(pos1, pos2);
			this.timer = timer;
		}

		private int getTimer() {
			return timer;
		}

		private void decreaseTimer() {
			timer--;
		}
	}
}
