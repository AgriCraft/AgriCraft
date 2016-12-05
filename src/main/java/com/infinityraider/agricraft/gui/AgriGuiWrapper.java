/*
 */
package com.infinityraider.agricraft.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;

/**
 *
 * @author Ryan
 */
public final class AgriGuiWrapper extends GuiContainer {

    private final IAgriGui gui;

    public AgriGuiWrapper(IAgriGui gui) {
        super(gui.getContainer());
        this.gui = gui;
        this.xSize = gui.getWidth();
        this.ySize = gui.getHeight();
    }

    public IAgriGui getGui() {
        return gui;
    }

    public final FontRenderer getFontRenderer() {
        return fontRendererObj;
    }

    public final RenderItem getItemRender() {
        return itemRender;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.gui.onGuiInit(this);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Calculate relative mouse position.
        final int relMouseX = mouseX - this.guiLeft;
        final int relMouseY = mouseY - this.guiTop;

        // Setup tooltip list.
        final List<String> toolTips = new ArrayList<>();

        // Call Mouse Moved Hook.
        this.gui.onUpdateMouse(this, toolTips, relMouseX, relMouseY);

        // Save renderer state.
        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        // Save renderer state.
        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        // Call render hook.
        gui.onRenderForeground(this, toolTips, relMouseX, relMouseY);

        // Restore renderer state.
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();

        // Draw current tooltip, if present.
        if (toolTips.size() > 0) {
            drawHoveringText(toolTips, relMouseX, relMouseY, fontRendererObj);
        }

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
        gui.onRenderBackground(this, f, relMouseX, relMouseY);

        // Restore renderer state.
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    @Override
    public final void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        // Calculate relative mouse position.
        final int relMouseX = mouseX - this.guiLeft;
        final int relMouseY = mouseY - this.guiTop;

        // Call mouse click hook.
        this.gui.onMouseClicked(this, relMouseX, relMouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        // Call mouse drag hook
        this.gui.onMouseClickMove(this, mouseX, mouseY, mouseY);
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
