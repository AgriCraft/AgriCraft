/*
 */
package com.infinityraider.agricraft.gui;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author Ryan
 */
@SideOnly(Side.CLIENT)
public final class AgriGuiWrapper extends GuiContainer {

    private final Deque<IAgriGui> guis;

    public AgriGuiWrapper(IAgriGui gui) {
        super(gui.getContainer());
        this.guis = new ArrayDeque<>();
        this.guis.add(gui);
        this.xSize = gui.getWidth();
        this.ySize = gui.getHeight();
    }

    public IAgriGui getGui() {
        return this.guis.getFirst();
    }

    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }

    public RenderItem getItemRender() {
        return itemRender;
    }

    public void pushGui(IAgriGui gui) {
        this.guis.add(gui);
        this.xSize = gui.getWidth();
        this.ySize = gui.getHeight();
        this.inventorySlots = gui.getContainer();
        this.initGui();
    }

    public void popGui() {
        if (this.guis.size() > 1) {
            this.guis.removeLast();
            this.xSize = guis.getLast().getWidth();
            this.ySize = guis.getLast().getHeight();
            this.inventorySlots = guis.getLast().getContainer();
            this.initGui();
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guis.getLast().onGuiInit(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawGuiContainerToolTipLayer(mouseX, mouseY);
    }
    
    protected void drawGuiContainerToolTipLayer(int mouseX, int mouseY) {
        // Save renderer state.
        GlStateManager.pushMatrix();

        // Translate to gui location.
        GlStateManager.translate(this.guiLeft, this.guiTop, 0);
        
        // Calculate relative mouse position.
        final int relMouseX = mouseX - this.guiLeft;
        final int relMouseY = mouseY - this.guiTop;
        
        // Setup tooltip list.
        final List<String> toolTips = new ArrayList<>();

        // Call Tooltip Hook
        this.guis.getLast().onRenderToolTips(this, toolTips, relMouseX, relMouseY);

        // Draw current tooltip, if present.
        if (toolTips.size() > 0) {
            drawHoveringText(toolTips, relMouseX, relMouseY, fontRenderer);
        }

        // Restore renderer state.
        GlStateManager.popMatrix();
        
        // Render other tooltips, if needed.
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Calculate relative mouse position.
        final int relMouseX = mouseX - this.guiLeft;
        final int relMouseY = mouseY - this.guiTop;

        // Call Mouse Moved Hook.
        this.guis.getLast().onUpdateMouse(this, relMouseX, relMouseY);

        // Save renderer state.
        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        // Call render hook.
        this.guis.getLast().onRenderForeground(this, relMouseX, relMouseY);

        // Restore renderer state.
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        // Calculate relative mouse position.
        final int relMouseX = mouseX - this.guiLeft;
        final int relMouseY = mouseY - this.guiTop;

        // Save renderer state.
        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        // Translate to gui location.
        GlStateManager.translate(this.guiLeft, this.guiTop, 0);

        // Call render hook.
        this.guis.getLast().onRenderBackground(this, f, relMouseX, relMouseY);

        // Restore renderer state.
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // Calculate relative mouse position.
        final int relMouseX = mouseX - this.guiLeft;
        final int relMouseY = mouseY - this.guiTop;

        // Call mouse click hook.
        this.guis.getLast().onMouseClicked(this, relMouseX, relMouseY, mouseButton);

        // Backpropagate
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        // Call mouse drag hook
        this.guis.getLast().onMouseClickMove(this, mouseX, mouseY, mouseButton);

        // Backpropagate
        super.mouseClickMove(mouseX, mouseY, mouseButton, timeSinceLastClick);
    }

    @Override
    protected void keyTyped(char character, int keycode) throws IOException {
        // Intercept Escape
        if (keycode == 1 && this.guis.size() > 1) {
            this.popGui();
            return;
        }

        // Call Hook
        this.guis.getLast().onKeyTyped(this, character, keycode);

        // Backpropagate
        super.keyTyped(character, keycode);
    }

    @Override
    public void drawHoveringText(List<String> lines, int x, int y) {
        super.drawHoveringText(lines, x, y);
    }

    public void drawRectangle(int x, int y, int width, int height, int color) {
        this.drawVerticalLine(x, y, y + height, color);
        this.drawHorizontalLine(x, x + width, y, color);
        this.drawVerticalLine(x + width, y, y + height, color);
        this.drawHorizontalLine(x, x + width, y + height, color);
    }

}
