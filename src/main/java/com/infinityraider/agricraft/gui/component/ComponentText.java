/*
 */
package com.infinityraider.agricraft.gui.component;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.gui.GuiBase;
import com.infinityraider.agricraft.utility.GuiHelper;
import java.awt.Rectangle;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author RlonRyan
 */
@SideOnly(Side.CLIENT)
public class ComponentText implements IComponent<String> {

	private final String text;
	private final Rectangle bounds;
	private final double scale;
	private final boolean centered;

	public ComponentText(String text, int x, int y, double scale, boolean centered) {
		this.text = AgriCore.getTranslator().translate(text);
		this.bounds = new Rectangle(x, y, 0, 0);
		this.scale = scale;
		this.centered = centered;
	}

	@Override
	public String getComponent() {
		return this.text;
	}

	@Override
	public Rectangle getBounds() {
		return this.bounds;
	}

	@Override
	public void renderComponent(GuiBase gui) {
		int x = this.bounds.x;
		int y = this.bounds.y;
		final FontRenderer fontRenderer = gui.getFontRenderer();

		// Prep
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();
		GlStateManager.scale(scale, scale, scale);
		List<String> write = GuiHelper.getLinesFromData(GuiHelper.splitInLines(gui.getFontRenderer(), text, 95, scale));
		for (int i = 0; i < write.size(); i++) {
			String line = write.get(i);
			int xOffset = centered ? -fontRenderer.getStringWidth(line) / 2 : 0;
			int yOffset = i * fontRenderer.FONT_HEIGHT;
			fontRenderer.drawString(line, (int) (this.bounds.x / scale) + xOffset, (int) (this.bounds.y / scale) + yOffset, 1644054);    //1644054 means black
		}

		// Clean
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
	}

}
