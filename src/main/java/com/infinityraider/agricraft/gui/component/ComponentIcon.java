/*
 */
package com.infinityraider.agricraft.gui.component;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.gui.GuiBase;
import java.awt.Rectangle;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author RlonRyan
 */
@SideOnly(Side.CLIENT)
public class ComponentIcon implements IComponent<ResourceLocation> {

	private final ResourceLocation texture;
	private final Rectangle bounds;
	private final String tooltip;

	public ComponentIcon(ResourceLocation texture, int x, int y, int width, int height) {
		this.texture = texture;
		this.bounds = new Rectangle(x, y, width, height);
		this.tooltip = "";
	}

	public ComponentIcon(ResourceLocation texture, int x, int y, int width, int height, String tooltip) {
		this.texture = texture;
		this.bounds = new Rectangle(x, y, width, height);
		this.tooltip = AgriCore.getTranslator().translate(tooltip);
	}

	@Override
	public ResourceLocation getComponent() {
		return this.texture;
	}

	@Override
	public Rectangle getBounds() {
		return this.bounds;
	}

	@Override
	public void addToolTip(List<String> toolTip, EntityPlayer player) {
		if (!tooltip.isEmpty()) {
			toolTip.add(tooltip);
		}
	}

	@Override
	public void renderComponent(GuiBase gui) {
		GlStateManager.pushAttrib();
		GlStateManager.color(1, 1, 1, 1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		GuiBase.drawModalRectWithCustomSizedTexture(
				this.bounds.x,
				this.bounds.y,
				0,
				0,
				this.bounds.width,
				this.bounds.height,
				this.bounds.width,
				this.bounds.height
		);
		GlStateManager.popAttrib();
	}

}
