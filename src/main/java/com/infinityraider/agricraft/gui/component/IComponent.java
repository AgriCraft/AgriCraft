/*
 */
package com.infinityraider.agricraft.gui.component;

import com.infinityraider.agricraft.gui.GuiBase;
import java.awt.Rectangle;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author RlonRyan
 */
@SideOnly(Side.CLIENT)
public interface IComponent<T> {
	
	T getComponent();
	
	Rectangle getBounds();
	
	void renderComponent(GuiBase gui);
	
	default boolean isOverComponent(int x, int y) {
		return this.getBounds().contains(x, y);
	}
	
	default void addToolTip(List<String> toolTip, EntityPlayer player) {
		// NOPE
	}
	
	default boolean onClick(EntityPlayer player) {
		return false;
	}
	
}
