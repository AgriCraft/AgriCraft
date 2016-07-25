package com.infinityraider.agricraft.gui.component;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Deprecated
public class Component<C> {

	protected final int x;
	protected final int y;
	protected final int xSize;
	protected final int ySize;
	protected final float scale;
	protected final boolean centered;
	protected final C component;

	public Component(C component, int x, int y) {
		this(component, x, y, 1.0F, false);
	}

	public Component(C component, int x, int y, boolean centered) {
		this(component, x, y, 1.0F, centered);
	}

	public Component(C component, int x, int y, float scale) {
		this(component, x, y, scale, false);
	}

	public Component(C component, int x, int y, float scale, boolean centered) {
		this(component, x, y, -1, -1, scale, centered);
	}

	public Component(C component, int x, int y, int xSize, int ySize) {
		this(component, x, y, xSize, ySize, 1.0F, false);
	}

	public Component(C component, int x, int y, int xSize, int ySize, boolean centered) {
		this(component, x, y, xSize, ySize, 1.0F, centered);
	}

	public Component(C component, int x, int y, int xSize, int ySize, float scale) {
		this(component, x, y, xSize, ySize, scale, false);
	}

	public Component(C component, int x, int y, int xSize, int ySize, float scale, boolean centered) {
		this.component = component;
		this.x = x;
		this.y = y;
		this.xSize = xSize;
		this.ySize = ySize;
		this.scale = scale;
		this.centered = centered;
	}

	public int xOffset() {
		return this.x;
	}

	public int yOffset() {
		return this.y;
	}

	public int xSize() {
		return this.xSize;
	}

	public int ySize() {
		return this.ySize;
	}

	public float scale() {
		return this.scale;
	}

	public boolean centered() {
		return this.centered;
	}

	public C getComponent() {
		return this.component;
	}

	public boolean isOverComponent(int x, int y) {
		x = (int) (x / scale);
		y = (int) (y / scale);
		if (this.xSize < 0 || this.ySize < 0) {
			return false;
		}
		if (this.x <= x && this.x + xSize > x) {
			if (this.y <= y && this.y + ySize > y) {
				return true;
			}
		}
		return false;
	}
}
