/*
 */
package com.infinityraider.agricraft.gui.component;

import com.infinityraider.agricraft.gui.GuiBase;
import java.util.List;

/**
 *
 * @author RlonRyan
 */
public interface IComponent<T> {
	
	T getComponent();
	
	int getX();
	
	int getY();
	
	int getWidth();
	
	int getHeight();
	
	double getScale();
	
	boolean isCentered();
	
	void renderComponent(GuiBase gui);
	
	default boolean isOverComponent(int x, int y) {
		x = (int) (x / this.getScale());
		y = (int) (y / this.getScale());
		if (this.getWidth() < 0 || this.getHeight() < 0) {
			return false;
		}
		if (this.getX() <= x && this.getX() + this.getWidth() > x) {
			if (this.getY() <= y && this.getY() + this.getHeight() > y) {
				return true;
			}
		}
		return false;
	}
	
	default void addToolTip(List<String> toolTip) {
		// NOPE
		
	}
	
}
