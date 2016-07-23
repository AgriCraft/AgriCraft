/*
 */
package com.infinityraider.agricraft.gui.component;

import com.infinityraider.agricraft.gui.GuiBase;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

/**
 *
 * @author RlonRyan
 */
public class ComponentItem implements IComponent<ItemStack> {

	private final ItemStack stack;
	private final int x, y;
	private final double scale;
	private final boolean centered;
	
	public ComponentItem(ItemStack stack, int x, int y) {
		this(stack, x, y, 1, false);
	}
	
	public ComponentItem(ItemStack stack, int x, int y, double scale, boolean centered) {
		this.stack = stack;
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.centered = centered;
	}

	@Override
	public ItemStack getComponent() {
		return this.stack;
	}

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public int getWidth() {
		return 16;
	}

	@Override
	public int getHeight() {
		return 16;
	}

	@Override
	public double getScale() {
		return this.scale;
	}

	@Override
	public boolean isCentered() {
		return this.centered;
	}

	@Override
	public void renderComponent(GuiBase gui) {
		GlStateManager.pushAttrib();
		GlStateManager.resetColor();
		int x = gui.getAnchorX() + this.x;
		int y = gui.getAnchorY() + this.y;
		gui.getRenderItem().renderItemIntoGUI(stack, x, y);
		GlStateManager.popAttrib();
	}

	@Override
	public void addToolTip(List<String> toolTip) {
		toolTip.add(this.stack.getDisplayName());
	}

}
