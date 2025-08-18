package net.dugged.cutelessmod;

import net.dugged.cutelessmod.mixins.IGuiChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.command.CommandBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.stream.Stream;

public class AreaSelectionRenderer {
	public static void render(float partialTicks) {
		final Minecraft mc = Minecraft.getMinecraft();
		if (!(mc.currentScreen instanceof GuiChat)) {
			return;
		}

		final GuiChat chat = (GuiChat) mc.currentScreen;
		final String msg = ((IGuiChat) chat).getInputField().getText().trim();
		final String[] args = msg.split(" ");
		if (args.length == 0) {
			return;
		}

		final EntityPlayerSP player = mc.player;
		final double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		final double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		final double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

		BlockPos p0 = null;
		BlockPos p1 = null;
		BlockPos p2 = null;

		// @formatter:off
		try { p0 = CommandBase.parseBlockPos(player, args, 1, false); } catch (Exception ignored) { /*noop*/ }
		try { p1 = CommandBase.parseBlockPos(player, args, 4, false); } catch (Exception ignored) { /*noop*/ }
		try { p2 = CommandBase.parseBlockPos(player, args, 7, false); } catch (Exception ignored) { /*noop*/ }
		// @formatter:on

		GlStateManager.depthMask(false);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		GlStateManager.glLineWidth(3F);

		if (Stream.of("/clone", "/fill", "/setblock").anyMatch(s -> args[0].equals(s))) {
			boolean setblock = false;
			if (args[0].equals("/setblock")) {
				p1 = p0;
				setblock = true;
			}

			AxisAlignedBB origin = null;
			if (p0 != null && p1 != null) {
				// man this shit is getting uglier every time i fix more bugs xd
				if ((d0 >= 0 && d0 >= (int) d0 + 0.5) || (d0 < 0 && d0 >= (int) d0 - 0.5)) {
					if (args[1].contains("~")) {
						p0 = p0.add(-1, 0, 0);
						if (setblock) {
							p1 = p1.add(-1, 0, 0);
						}
					} else if (!setblock && args[4].contains("~")) {
						p1 = p1.add(-1, 0, 0);
					}
				}
				if ((d2 >= 0 && d2 >= (int) d2 + 0.5) || (d2 < 0 && d2 >= (int) d2 - 0.5)) {
					if (args[3].contains("~")) {
						p0 = p0.add(0, 0, -1);
						if (setblock) {
							p1 = p1.add(0, 0, -1);
						}
					} else if (!setblock && args[6].contains("~")) {
						p1 = p1.add(0, 0, -1);
					}
				}
				origin = new AxisAlignedBB(p0, p1);
				GlStateManager.depthFunc(GL11.GL_ALWAYS);
				RenderGlobal.drawSelectionBoundingBox(origin.expand(1F, 1F, 1F).offset(-d0, -d1, -d2), 0.9F, 0.9F, 0.9F, 1F);
				GlStateManager.depthFunc(GL11.GL_LEQUAL);
			}

			if (args[0].equals("/clone")) {
				if (p2 != null && origin != null) {
					if ((d0 >= 0 && d0 >= (int) d0 + 0.5) || (d0 < 0 && d0 >= (int) d0 - 0.5)) {
						if (args[7].contains("~")) {
							p2 = p2.add(-1, 0, 0);
						}
					}
					if ((d2 >= 0 && d2 >= (int) d2 + 0.5) || (d2 < 0 && d2 >= (int) d2 - 0.5)) {
						if (args[9].contains("~")) {
							p2 = p2.add(0, 0, -1);
						}
					}
					final AxisAlignedBB target = new AxisAlignedBB(p2, p2.add(origin.maxX - origin.minX + 1, origin.maxY - origin.minY + 1, origin.maxZ - origin.minZ + 1));
					GlStateManager.depthFunc(GL11.GL_ALWAYS);
					RenderGlobal.drawSelectionBoundingBox(target.grow(0.005F).offset(-d0, -d1, -d2), 0.99F, 0.99F, 0.99F, 1F);
				}	GlStateManager.depthFunc(GL11.GL_LEQUAL);
			}
		}

		GlStateManager.glLineWidth(1F);
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}
}
