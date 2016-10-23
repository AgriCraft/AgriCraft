/*
 */
package com.infinityraider.agricraft.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderItem;

/**
 *
 * 
 */
public abstract class GuiBase extends GuiScreen {
	
	public abstract int getAnchorX();
	
	public abstract int getAnchorY();
	
	public FontRenderer getFontRenderer() {
		return this.fontRendererObj;
	}
	
	public RenderItem getRenderItem() {
		return this.itemRender;
	}
	
}
