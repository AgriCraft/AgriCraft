/*
 */
package com.infinityraider.agricraft.gui.component;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.gui.GuiBase;
import com.infinityraider.agricraft.utility.GuiHelper;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author RlonRyan
 */
public class ComponentText implements IComponent<List<String>> {

	private final List<String> text;
	private final int x, y;
	private final double scale;
	private final boolean centered;

	public ComponentText(String text, int x, int y, double scale, boolean centered) {
		this(Arrays.asList(text), x, y, scale, centered);
	}
	
	public ComponentText(List<String> text, int x, int y, double scale, boolean centered) {
		this.text = text.stream().map(AgriCore.getTranslator()::translate).collect(Collectors.toList());
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.centered = centered;
	}

	@Override
	public List<String> getComponent() {
		return this.text;
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
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
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
		int x = gui.getAnchorX() + this.x;
		int y = gui.getAnchorY() + this.y;
		final FontRenderer fontRenderer = gui.getFontRenderer();
		
		// Prep
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();
		
		GL11.glScaled(scale, scale, scale);
		for (String paragraph : text) {
			List<String> write = GuiHelper.getLinesFromData(GuiHelper.splitInLines(gui.getFontRenderer(), paragraph, 95, scale));
			for (int i = 0; i < write.size(); i++) {
				String line = write.get(i);
				int xOffset = centered ? -fontRenderer.getStringWidth(line) / 2 : 0;
				int yOffset = i * fontRenderer.FONT_HEIGHT;
				fontRenderer.drawString(line, (int) (x / scale) + xOffset, (int) (y / scale) + yOffset, 1644054);    //1644054 means black
			}
			y = y + (int) ((float) fontRenderer.FONT_HEIGHT / scale);
		}
		
		// Clean
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
	}

}
