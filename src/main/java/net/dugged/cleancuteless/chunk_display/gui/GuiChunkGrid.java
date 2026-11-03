package net.dugged.cleancuteless.chunk_display.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class GuiChunkGrid extends GuiScreen {

	private static final String[] MINIMAP_NAMES = {"Minimap: OFF", "Minimap: Follow", "Minimap: Static"};
	private static final String[] DIMENSION_NAMES = {"Overworld", "Nether", "End"};
	private static final int PANEL_BACKGROUND = 0xF0101010;
	private static final int PANEL_BORDER = 0xFF4F4F4F;
	private static final int TEXT_COLOR = 0xFFE6E6E6;
	private static final int TEXT_MUTED = 0xFFA8A8A8;
	// Footer is a grid, makes stuff easier
	private static final int HEADER_HEIGHT = 16;
	private static final int FOOTER_ROW_HEIGHT = 20;
	private static final int FOOTER_ROW_PADDING = 5;
	private static final int FOOTER_ROW_COUNT = 3;
	private static final int FOOTER_HEIGHT = FOOTER_ROW_HEIGHT * FOOTER_ROW_COUNT + FOOTER_ROW_PADDING * (FOOTER_ROW_COUNT + 1);
	private static final int FOOTER_COL_PADDING = 5;
	private static final int FOOTER_COL_COUNT = 5;
	private static final int MINIMAP_MARGIN = 8;
	private static final int MINIMAP_MIN_SIZE = 120;
	private static final int MINIMAP_MAX_SIZE = 220;
	public static GuiChunkGrid instance;
	public static ChunkGridStyle style = ChunkGridStyle.CHECKERBOARD;
	//	public static ChunkGridStyle style = ChunkGridStyle.values()[new Random().nextInt(ChunkGridStyle.values().length)];
	private final Controller controller;
	private final ChunkGrid chunkgrid = new ChunkGrid();
	private final ChunkGrid minimapGrid = new ChunkGrid(6);
	private GuiButton startStopButton;
	private GuiButton loadButton;
	private GuiButton saveButton;
	private GuiButton backButton;
	private GuiButton forwardButton;
	private GuiButton currentButton;
	private GuiButton beginingButton;
	private GuiButton endButton;
	private GuiButton playButton;
	private GuiNumericTextField textFieldX;
	private GuiNumericTextField textFieldZ;
	private GuiNumericTextField textFieldGT;
	private int time;
	private int xText;
	private int zText;
	private int selectedMinimap = 0;
	private GuiButton minimapButton;
	private int selectedDimension = 0;
	private GuiButton dimensionButton;
	private boolean chunkSelection;
	private String chunkSelectionString;
	private int minimapWidth;
	private int minimapHeight;
	private boolean consumeLeftClickOnes = false;

	public GuiChunkGrid() {
		this.controller = new Controller(this);
	}

	// GUI functions

	@Override
	public void initGui() {
		super.initGui();

		addButton(startStopButton = new GuiButton(0, getFooterX(0), getFooterY(0), getFooterColWidth(), FOOTER_ROW_HEIGHT, controller.start ? "Stop" : "Start"));

		addButton(minimapButton = new GuiButton(1, getFooterX(1), getFooterY(0), getFooterColWidth(), FOOTER_ROW_HEIGHT, MINIMAP_NAMES[selectedMinimap]));

		addButton(loadButton = new GuiButton(2, getFooterX(2), getFooterY(0), getFooterColWidth(), FOOTER_ROW_HEIGHT, "Load"));
		addButton(saveButton = new GuiButton(3, getFooterX(3), getFooterY(0), getFooterColWidth(), FOOTER_ROW_HEIGHT, "Save"));
		addButton(currentButton = new GuiButton(4, getFooterX(4), getFooterY(0), getFooterColWidth(), FOOTER_ROW_HEIGHT, "Current"));


		addButton(backButton = new GuiButton(5, getFooterX(1), getFooterY(1), getFooterColWidth(), FOOTER_ROW_HEIGHT, "Back"));
		addButton(forwardButton = new GuiButton(6, getFooterX(2), getFooterY(1), getFooterColWidth(), FOOTER_ROW_HEIGHT, "Forward"));

		addButton(beginingButton = new GuiButton(7, getFooterX(0), getFooterY(1), getFooterColWidth(), FOOTER_ROW_HEIGHT, "Beginning"));
		addButton(endButton = new GuiButton(8, getFooterX(3), getFooterY(1), getFooterColWidth(), FOOTER_ROW_HEIGHT, "End"));
		addButton(playButton = new GuiButton(9, getFooterX(4), getFooterY(1), getFooterColWidth(), FOOTER_ROW_HEIGHT, controller.play ? "Pause" : "Play"));

		addButton(new GuiButton(10, getFooterX(0), getFooterY(2), getFooterColWidth(), FOOTER_ROW_HEIGHT, "Home"));
		addButton(dimensionButton = new GuiButton(11, getFooterX(1), getFooterY(2), getFooterColWidth(), FOOTER_ROW_HEIGHT, DIMENSION_NAMES[selectedDimension]));

		textFieldX = new GuiNumericTextField(12, mc.fontRenderer, getFooterXTextField(2), getFooterY(2), getFooterColWidthTextField(), FOOTER_ROW_HEIGHT) {
			@Override
			public void performTextAction() {
				controller.setX(getText());
			}
		};
		textFieldZ = new GuiNumericTextField(13, mc.fontRenderer, getFooterXTextField(3), getFooterY(2), getFooterColWidthTextField(), FOOTER_ROW_HEIGHT) {
			@Override
			public void performTextAction() {
				controller.setZ(getText());
			}
		};
		textFieldGT = new GuiNumericTextField(14, mc.fontRenderer, getFooterXTextField(4), getFooterY(2), getFooterColWidthTextField(), FOOTER_ROW_HEIGHT) {
			@Override
			public void performTextAction() {
				controller.setTime(getText());
			}
		};

		loadButton.enabled = saveButton.enabled = playButton.enabled = !controller.start;
		currentButton.enabled = controller.start;

		controller.updateGUI();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
			case 0:
				if (controller.startStop()) {
					startStopButton.displayString = "Stop";
					loadButton.enabled = saveButton.enabled = playButton.enabled = false;
					currentButton.enabled = true;
				} else {
					startStopButton.displayString = "Start";
					loadButton.enabled = saveButton.enabled = playButton.enabled = true;
					currentButton.enabled = false;
				}
				break;
			case 1:
				selectedMinimap = (selectedMinimap + 1) % MINIMAP_NAMES.length;
				minimapButton.displayString = MINIMAP_NAMES[selectedMinimap];
				if (selectedMinimap != 0) {
					controller.updateMinimap();
				}
				break;
			case 2:
				controller.load();
				break;
			case 3:
				controller.save();
				break;
			case 4:
				controller.current();
				break;
			case 5:
				controller.back();
				break;
			case 6:
				controller.forward();
				break;
			case 7:
				controller.begining();
				break;
			case 8:
				controller.end();
				break;
			case 9:
				controller.play();
				if (controller.play) {
					playButton.displayString = "Pause";
				} else {
					playButton.displayString = "Play";
				}
				break;
			case 10:
				controller.home();
				break;
			case 11:
				selectedDimension = (selectedDimension + 1) % DIMENSION_NAMES.length;
				dimensionButton.displayString = DIMENSION_NAMES[selectedDimension];
				controller.dimentionUpdate();
				break;
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		final int scroll = Mouse.getEventDWheel();
		if (scroll == 0) {
			return;
		}

		final int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
		final int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		if (mouseY >= HEADER_HEIGHT && mouseY < this.height - FOOTER_HEIGHT) {
			controller.scroll(scroll > 0 ? 1 : -1);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		textFieldGT.mouseClicked(mouseX, mouseY, mouseButton);
		textFieldX.mouseClicked(mouseX, mouseY, mouseButton);
		textFieldZ.mouseClicked(mouseX, mouseY, mouseButton);
		if (mouseY >= HEADER_HEIGHT && mouseY < height - FOOTER_HEIGHT)
			controller.buttonDown(mouseX, mouseY - HEADER_HEIGHT, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		super.mouseReleased(mouseX, mouseY, mouseButton);
		if (mouseY >= HEADER_HEIGHT && mouseY < height - FOOTER_HEIGHT && !consumeLeftClickOnes)
			controller.buttonUp(mouseX, mouseY - HEADER_HEIGHT, mouseButton);
		consumeLeftClickOnes = false;
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		if (mouseY >= HEADER_HEIGHT && mouseY < height - FOOTER_HEIGHT)
			controller.mouseDrag(mouseX, mouseY - HEADER_HEIGHT, clickedMouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == Keyboard.KEY_F6) {
			mc.displayGuiScreen(null);
		}
		textFieldGT.keyDown(typedChar, keyCode);
		textFieldX.keyDown(typedChar, keyCode);
		textFieldZ.keyDown(typedChar, keyCode);
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Gui.drawRect(0, 0, this.width, this.height, PANEL_BACKGROUND);
		chunkgrid.draw(controller.getView(), 0, HEADER_HEIGHT, this.width, this.height - HEADER_HEIGHT - FOOTER_HEIGHT);

		drawPanel(0, 0, this.width, HEADER_HEIGHT);
		drawPanel(0, this.height - FOOTER_HEIGHT, this.width, this.height);

		drawCenteredString(fontRenderer, "Chunk Debug Map", this.width / 2, 4, TEXT_COLOR);
		final String status = controller.isRecording() ? "Recording" : "Idle";
		drawString(fontRenderer, status, this.width - 6 - fontRenderer.getStringWidth(status), 4, controller.isRecording() ? 0xFF55FF55 : TEXT_MUTED);

		if (chunkSelection) {
			final int textWidth = fontRenderer.getStringWidth(chunkSelectionString);
			final int x = this.width / 2 - textWidth / 2 - 4;
			Gui.drawRect(x, HEADER_HEIGHT + 3, x + textWidth + 8, HEADER_HEIGHT + 15, 0xC0000000);
			drawCenteredString(fontRenderer, chunkSelectionString, this.width / 2, HEADER_HEIGHT + 6, TEXT_COLOR);
		}

		drawCenteredString(fontRenderer, "X:", getFooterX(2) + 5, getFooterY(2) + FOOTER_ROW_HEIGHT / 2 - 4, TEXT_COLOR);
		textFieldX.drawTextBox();
		drawCenteredString(fontRenderer, "Z:", getFooterX(3) + 5, getFooterY(2) + FOOTER_ROW_HEIGHT / 2 - 4, TEXT_COLOR);
		textFieldZ.drawTextBox();
		drawCenteredString(fontRenderer, "GT:", getFooterX(4) + 4, getFooterY(2) + FOOTER_ROW_HEIGHT / 2 - 4, TEXT_COLOR);
		textFieldGT.drawTextBox();

		super.drawScreen(mouseX, mouseY, partialTicks);

		for (final GuiButton button : this.buttonList) {
			if (mouseX >= button.x && mouseY >= button.y && mouseX < button.x + button.width && mouseY < button.y + button.height) {
				final String tooltip = getButtonTooltip(button.id);
				if (tooltip != null) {
					drawHoveringText(tooltip, mouseX, mouseY);
				}
				break;
			}
		}
	}

	private static String getButtonTooltip(final int id) {
		switch (id) {
			case 0:
				return "Start or stop recording chunk events.";
			case 1:
				return "Cycle the minimap overlay mode.";
			case 2:
				return "Load a saved chunk recording.";
			case 3:
				return "Save the current chunk recording.";
			case 4:
				return "Jump to the latest recorded tick.";
			case 5:
				return "Jump to the previous recorded tick.";
			case 6:
				return "Jump to the next recorded tick.";
			case 7:
				return "Jump to the first recorded tick.";
			case 8:
				return "Jump to the last recorded tick.";
			case 9:
				return "Play or pause the replay.";
			case 10:
				return "Center the map on the player.";
			case 11:
				return "Cycle the displayed dimension.";
			default:
				return null;
		}
	}

	private void drawPanel(final int left, final int top, final int right, final int bottom) {
		Gui.drawRect(left, top, right, bottom, PANEL_BACKGROUND);
		Gui.drawRect(left, top, right, top + 1, PANEL_BORDER);
		Gui.drawRect(left, bottom - 1, right, bottom, PANEL_BORDER);
		Gui.drawRect(left, top, left + 1, bottom, PANEL_BORDER);
		Gui.drawRect(right - 1, top, right, bottom, PANEL_BORDER);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void onGuiClosed() {
		controller.updateMinimap();
	}

	// Minimap

	public void renderMinimap() {
		if (isChunkDebugWindowOpen()) {
			return;
		}

		final ScaledResolution resolution = new ScaledResolution(mc);
		final int screenWidth = resolution.getScaledWidth();
		final int screenHeight = resolution.getScaledHeight();
		final int size = MathHelper.clamp((int) (Math.min(screenWidth, screenHeight) * 0.32F), MINIMAP_MIN_SIZE, MINIMAP_MAX_SIZE);
		final int x = screenWidth - size - MINIMAP_MARGIN;
		final int y = MINIMAP_MARGIN;
		final int gridHeight = size - 12;

		if (minimapWidth != size || minimapHeight != gridHeight) {
			minimapWidth = size;
			minimapHeight = gridHeight;
			controller.updateMinimap();
		}

		drawMinimapPanel(x, y, size);
		minimapGrid.draw(controller.getMinimapView(), x, y + 12, size, gridHeight);

		if (!controller.hasData()) {
			drawCenteredString(fontRenderer, "No chunk data", x + size / 2, y + 12 + gridHeight / 2 - 4, TEXT_MUTED);
		}
	}

	private void drawMinimapPanel(final int x, final int y, final int size) {
		Gui.drawRect(x - 1, y - 1, x + size + 1, y + size + 1, 0xFF000000);
		Gui.drawRect(x, y, x + size, y + size, 0xE0101010);
		Gui.drawRect(x, y + 11, x + size, y + 12, PANEL_BORDER);
		drawString(fontRenderer, "Chunk Map", x + 4, y + 2, TEXT_COLOR);
		final String mode = selectedMinimap == 1 ? "Follow" : "Static";
		drawString(fontRenderer, mode, x + size - 4 - fontRenderer.getStringWidth(mode), y + 2, TEXT_MUTED);
	}

	public void tickMinimap() {
		if (selectedMinimap != 0 && !isChunkDebugWindowOpen()) {
			controller.updateMinimap();
		}
	}

	private int getFooterColWidth() {
		return (width - FOOTER_ROW_PADDING * (FOOTER_COL_COUNT + 1)) / FOOTER_COL_COUNT;
	}

	private int getFooterX(int x) {
		return x * getFooterColWidth() + (x + 1) * FOOTER_COL_PADDING;
	}

	private int getFooterXTextField(int x) {
		return x * getFooterColWidth() + (x + 1) * FOOTER_COL_PADDING + 13;
	}

	private int getFooterY(int y) {
		return height - FOOTER_HEIGHT + y * FOOTER_ROW_HEIGHT + (y + 1) * FOOTER_ROW_PADDING;
	}

	private int getFooterColWidthTextField() {
		return (width - FOOTER_ROW_PADDING * (FOOTER_COL_COUNT + 1)) / FOOTER_COL_COUNT - 15;
	}

	// Accessors

	public boolean isChunkDebugWindowOpen() {
		return mc.currentScreen == this || mc.currentScreen instanceof GuiSubWindow;
	}

	public ChunkGrid getChunkGrid() {
		return chunkgrid;
	}

	public ChunkGrid getMinimapGrid() {
		return minimapGrid;
	}

	public int getSelectedDimension() {
		return selectedDimension;
	}

	public void setSelectedDimension(int dimension) {
		selectedDimension = dimension;
		dimensionButton.displayString = DIMENSION_NAMES[selectedDimension];
	}

	public void setTime(int time) {
		this.textFieldGT.setText(Integer.toString(time));
	}

	public void setXText(int xText) {
		this.textFieldX.setText(Integer.toString(xText));
	}

	public void setZText(int zText) {
		this.textFieldZ.setText(Integer.toString(zText));
	}

	public void setBackButtonText(String text) {
		this.backButton.displayString = text;
	}

	public void setForwardButtonText(String text) {
		this.forwardButton.displayString = text;
	}

	public void liveUpdate() {
		controller.liveUpdate();
	}

	public void setPlayButtonText(String text) {
		playButton.displayString = text;
	}

	public int getMinimapWidth() {
		return minimapWidth;
	}

	public int getMinimapHeight() {
		return minimapHeight;
	}

	public int windowWidth() {
		return width;
	}

	public int windowHeight() {
		return height - HEADER_HEIGHT - FOOTER_HEIGHT;
	}

	public void consumeLeftClick() {
		consumeLeftClickOnes = true;
	}

	public int getMinimapType() {
		return selectedMinimap;
	}

	public void selectedChunk(boolean cs, int x, int y) {
		chunkSelection = cs;
		chunkSelectionString = "Chunk selected X: " + x + " Z: " + y;
	}

	public void disableDebugger() {
		startStopButton.displayString = "Start";
		loadButton.enabled = saveButton.enabled = playButton.enabled = true;
		currentButton.enabled = false;
		controller.setStart(false);
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("Chunk Debug Tool disabled on the server, enable chunkDebugTool rule for use."));
	}

	public Controller getController() {
		return controller;
	}
}
