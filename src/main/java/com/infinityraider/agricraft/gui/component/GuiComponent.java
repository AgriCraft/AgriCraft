/*
 */
package com.infinityraider.agricraft.gui.component;

import com.infinityraider.agricraft.gui.AgriGuiWrapper;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 *
 */
@SideOnly(Side.CLIENT)
public class GuiComponent<C> {

    private final C component;
    private final Rectangle bounds;
    private final Rectangle uv;
    private final double scale;
    private final boolean centered;

    private BiConsumer<GuiComponent<C>, List<String>> tootipAdder;
    private BiFunction<GuiComponent<C>, Point, Boolean> mouseClickAction;
    private BiConsumer<GuiComponent<C>, Point> mouseEnterAction;
    private BiConsumer<GuiComponent<C>, Point> mouseLeaveAction;
    private BiConsumer<AgriGuiWrapper, GuiComponent<C>> renderAction;

    private boolean isVisible;
    private boolean isEnabled;
    private boolean isHovered = false;

    public GuiComponent(C component, Rectangle bounds, Rectangle uv, double scale, boolean centered, boolean visable, boolean enabled, BiConsumer<GuiComponent<C>, List<String>> tootipAdder, BiFunction<GuiComponent<C>, Point, Boolean> mouseClickAction, BiConsumer<GuiComponent<C>, Point> mouseEnterAction, BiConsumer<GuiComponent<C>, Point> mouseLeaveAction, BiConsumer<AgriGuiWrapper, GuiComponent<C>> renderAction) {
        this.component = component;
        this.bounds = bounds;
        this.uv = uv;
        this.scale = scale;
        this.centered = centered;
        this.isVisible = visable;
        this.isEnabled = enabled;
        this.tootipAdder = tootipAdder;
        this.mouseClickAction = mouseClickAction;
        this.mouseEnterAction = mouseEnterAction;
        this.mouseLeaveAction = mouseLeaveAction;
        this.renderAction = renderAction;
    }

    public final C getComponent() {
        return this.component;
    }

    public final Rectangle getBounds() {
        return bounds;
    }

    public Rectangle getUV() {
        return uv;
    }

    public double getScale() {
        return scale;
    }

    public final boolean isCentered() {
        return centered;
    }

    public final boolean isHovered() {
        return isHovered;
    }

    public final boolean isVisable() {
        return isVisible;
    }

    public final boolean isEnabled() {
        return isEnabled;
    }

    public final void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public final void setVisable(boolean visable) {
        this.isVisible = visable;
    }

    public void setTootipAdder(BiConsumer<GuiComponent<C>, List<String>> tootipAdder) {
        this.tootipAdder = tootipAdder;
    }

    public void setMouseClickAction(BiFunction<GuiComponent<C>, Point, Boolean> mouseClickAction) {
        this.mouseClickAction = mouseClickAction;
    }

    public void setMouseEnterAction(BiConsumer<GuiComponent<C>, Point> mouseEnterAction) {
        this.mouseEnterAction = mouseEnterAction;
    }

    public void setMouseLeaveAction(BiConsumer<GuiComponent<C>, Point> mouseLeaveAction) {
        this.mouseLeaveAction = mouseLeaveAction;
    }

    public void setRenderAction(BiConsumer<AgriGuiWrapper, GuiComponent<C>> renderAction) {
        this.renderAction = renderAction;
    }

    public final boolean contains(int x, int y) {
        return this.getBounds().contains(x, y);
    }

    public final boolean onClick(int x, int y, int mouseButton) {
        return this.isEnabled && this.mouseClickAction != null && this.mouseClickAction.apply(this, relativize(x, y));
    }

    public final void onMouseMove(int x, int y) {
        if (this.isEnabled) {
            if (contains(x, y)) {
                if (!this.isHovered) {
                    this.isHovered = true;
                    this.onMouseEnter(x, y);
                }
            } else if (this.isHovered) {
                this.isHovered = false;
                this.onMouseLeave(x, y);
            }
        }
    }

    public final void onMouseEnter(int x, int y) {
        if (this.isEnabled && this.mouseEnterAction != null) {
            this.mouseEnterAction.accept(this, this.relativize(x, y));
        }
    }

    public final void onMouseLeave(int x, int y) {
        if (this.isEnabled && this.mouseLeaveAction != null) {
            this.mouseLeaveAction.accept(this, this.relativize(x, y));
        }
    }

    public final void addToolTip(List<String> toolTip, EntityPlayer player) {
        if (this.isEnabled && this.tootipAdder != null) {
            this.tootipAdder.accept(this, toolTip);
        }
    }

    public final void renderComponent(AgriGuiWrapper gui) {
        if (this.isVisible && this.renderAction != null) {
            GlStateManager.pushAttrib();
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.bounds.x, this.bounds.y, 0);
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.color(1, 1, 1, 1);
            this.renderAction.accept(gui, this);
            GlStateManager.popMatrix();
            GlStateManager.popAttrib();
        }
    }

    public final Point relativize(int x, int y) {
        return new Point(x - this.bounds.x, y - this.bounds.y);
    }

}
