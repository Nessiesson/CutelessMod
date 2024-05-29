package net.dugged.cutelessmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.ArrayList;

public class SphereRenderer {
	private final KeyBinding sphereKey = new KeyBinding("Display despawn sphere", KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, Reference.NAME);
	private final KeyBinding disableKey = new KeyBinding("Toggle despawn sphere", KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, Reference.NAME);
	private boolean isVisible = false;

	private EntityPlayer player;
	private double renderPosX;
	private double renderPosY;
	private double renderPosZ;
	private final double radius = 128.0;
	private BlockPos position;
	private final ArrayList<AxisAlignedBB> points = new ArrayList<>(1681);

	public void init(File configPath) {
		LiteLoader.getInput().registerKeyBinding(this.sphereKey);
		LiteLoader.getInput().registerKeyBinding(this.disableKey);
	}

	private void renderSphere(ArrayList<AxisAlignedBB> pointsInSphere) {
		for (final AxisAlignedBB bb : pointsInSphere) {
			RenderGlobal.drawSelectionBoundingBox(bb.grow(0.002).offset(-this.renderPosX, -this.renderPosY, -this.renderPosZ), 1.0F, 1.0F, 1.0F, 0.11F);
		}
	}

	private void render(float partialTicks) {
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.glLineWidth(2.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		RenderGlobal.drawSelectionBoundingBox(
				(new AxisAlignedBB(this.position.getX(), this.position.getY(), this.position.getZ(), this.position.getX() + 1, this.position.getY() + 2, this.position.getZ() + 1))
						.grow(-0.1).offset(-this.renderPosX, -this.renderPosY, -this.renderPosZ), 1.0F, 1.0F, 1.0F, 1.0F);
		this.renderSphere(this.points);
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
		if (inGame) {
			this.player = Minecraft.getMinecraft().player;
			this.renderPosX = this.player.lastTickPosX + (this.player.posX - this.player.lastTickPosX) * (double) partialTicks;
			this.renderPosY = this.player.lastTickPosY + (this.player.posY - this.player.lastTickPosY) * (double) partialTicks;
			this.renderPosZ = this.player.lastTickPosZ + (this.player.posZ - this.player.lastTickPosZ) * (double) partialTicks;
			if (this.sphereKey.isPressed()) {
				this.isVisible = true;
				this.position = new BlockPos(this.player.posX, this.player.posY, this.player.posZ);
				this.points.clear();
				int squareOfDistance = 80;

				for (int x = this.position.getX() - squareOfDistance; x <= this.position.getX() + squareOfDistance; ++x) {
					for (int z = this.position.getZ() - squareOfDistance; z <= this.position.getZ() + squareOfDistance; ++z) {
						final double r2 = this.radius * this.radius;
						final double x2 = this.position.getX() * this.position.getX();
						final double z2 = this.position.getZ() * this.position.getZ();
						AxisAlignedBB b = new AxisAlignedBB(new BlockPos(
								x,
								(float) (this.position.getY() - 1) - MathHelper.sqrt(r2 - x2 - z2 + (double) (2 * this.position.getX() * x) - (double) (x * x) + (double) (2 * this.position.getZ() * z) - (double) (z * z)),
								z));
						this.points.add(b);
					}
				}
			}

			if (this.disableKey.isPressed()) {
				this.isVisible = !this.isVisible;
			}
		} else {
			this.isVisible = false;
		}

	}

	public void onPostRender(float partialTicks) {
		if (this.isVisible) {
			this.render(partialTicks);
		}
	}
}
