/*
 */
package com.infinityraider.agricraft.gui.component;

import com.infinityraider.agricraft.gui.AgriGuiWrapper;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;


public class GuiComponentBuilder<C> {

	private final C component;
	private final Rectangle bounds;
	private Rectangle uv = new Rectangle(0, 0, 16, 16);
	private double scale = 1;
	private boolean centered = false;
	private boolean visible = true;
	private boolean enabled = true;
	private BiConsumer<GuiComponent<C>, List<String>> tootipAdder = null;
	private BiFunction<GuiComponent<C>, Point, Boolean> mouseClickAction = null;
	private BiConsumer<GuiComponent<C>, Point> mouseEnterAction = null;
	private BiConsumer<GuiComponent<C>, Point> mouseLeaveAction = null;
	private BiConsumer<AgriGuiWrapper, GuiComponent<C>> renderAction = null;

	public GuiComponentBuilder(C component, int x, int y, int width, int height) {
		this.component = component;
		this.bounds = new Rectangle(x, y, width, height);
	}

	public GuiComponentBuilder<C> setUV(int u, int v, int width, int height) {
		this.uv = new Rectangle(u, v, width, height);
		return this;
	}

	public GuiComponentBuilder<C> setScale(double scale) {
		this.scale = scale;
		return this;
	}

	public GuiComponentBuilder<C> setCentered(boolean centered) {
		this.centered = centered;
		return this;
	}
	
	public GuiComponentBuilder<C> setVisable(boolean visable) {
		this.visible = visable;
		return this;
	}
	
	public GuiComponentBuilder<C> setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public GuiComponentBuilder<C> setTootipAdder(BiConsumer<GuiComponent<C>, List<String>> tootipAdder) {
		this.tootipAdder = tootipAdder;
		return this;
	}

	public GuiComponentBuilder<C> setMouseClickAction(BiFunction<GuiComponent<C>, Point, Boolean> mouseClickAction) {
		this.mouseClickAction = mouseClickAction;
		return this;
	}

	public GuiComponentBuilder<C> setMouseEnterAction(BiConsumer<GuiComponent<C>, Point> mouseEnterAction) {
		this.mouseEnterAction = mouseEnterAction;
		return this;
	}

	public GuiComponentBuilder<C> setMouseLeaveAction(BiConsumer<GuiComponent<C>, Point> mouseLeaveAction) {
		this.mouseLeaveAction = mouseLeaveAction;
		return this;
	}

	public GuiComponentBuilder<C> setRenderAction(BiConsumer<AgriGuiWrapper, GuiComponent<C>> renderAction) {
		this.renderAction = renderAction;
		return this;
	}

	public GuiComponent<C> build() {
		return new GuiComponent<>(component, bounds, uv, scale, centered, visible, enabled, tootipAdder, mouseClickAction, mouseEnterAction, mouseLeaveAction, renderAction);
	}
	
}
