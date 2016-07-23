/*
 */
package com.infinityraider.agricraft.gui.component;

import com.infinityraider.agricraft.gui.GuiBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 *
 * @author RlonRyan
 */
public class ComponentTexture implements IComponent<ResourceLocation> {

	private final ResourceLocation texture;
	private final int x, y;
	private final int width, height;
	private final double scale;
	private final boolean centered;

	public ComponentTexture(ResourceLocation texture, int x, int y, int width, int height) {
		this(texture, x, y, width, height, 1, false);
	}

	public ComponentTexture(ResourceLocation texture, int x, int y, int width, int height, double scale, boolean centered) {
		this.texture = texture;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.scale = scale;
		this.centered = centered;
	}

	@Override
	public ResourceLocation getComponent() {
		return this.texture;
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
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
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
		GlStateManager.color(1, 1, 1, 1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		GuiBase.drawModalRectWithCustomSizedTexture(
				gui.getAnchorX() + x,
				gui.getAnchorY() + y,
				0,
				0,
				width,
				height,
				width,
				height
		);
		GlStateManager.popAttrib();
	}

}
