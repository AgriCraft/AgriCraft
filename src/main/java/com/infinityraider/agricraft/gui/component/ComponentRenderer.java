/*
 */
package com.infinityraider.agricraft.gui.component;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.gui.AgriGuiWrapper;
import com.infinityraider.agricraft.utility.GuiHelper;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 *
 * 
 */
public final class ComponentRenderer {
	
	public static final ResourceLocation FRAME_ICON = new ResourceLocation("agricraft:textures/gui/journal/GuiJournalSeedFrame.png");

	public static void renderIconComponent(AgriGuiWrapper gui, GuiComponent<ResourceLocation> component) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(component.getComponent());
		gui.drawModalRectWithCustomSizedTexture(
				0,
				0,
				0,
				0,
				component.getBounds().width,
				component.getBounds().height,
				component.getBounds().width,
				component.getBounds().height
		);
	}
    
    public static void renderComponentButton(AgriGuiWrapper gui, GuiComponent<String> component) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(GuiButton.ICONS);
		gui.drawModalRectWithCustomSizedTexture(
				0,
				0,
				0,
				0,
				component.getBounds().width,
				component.getBounds().height,
				component.getBounds().width,
				component.getBounds().height
		);
        ComponentRenderer.renderComponentText(gui, component);
    }

	public static void renderComponentStackFramed(AgriGuiWrapper gui, GuiComponent<ItemStack> component) {
		renderStackFrame(gui, component);
		renderComponentStack(gui, component);
	}

	public static void renderComponentStack(AgriGuiWrapper gui, GuiComponent<ItemStack> component) {
		gui.getItemRender().renderItemAndEffectIntoGUI(component.getComponent(), 0, 0);
        GlStateManager.enableAlpha();
	}

	public static void renderStackFrame(AgriGuiWrapper gui, GuiComponent<ItemStack> component) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(FRAME_ICON);
		gui.drawModalRectWithCustomSizedTexture(-1, -1, 0, 0, 18, 18, 18, 18);
	}
	
	public static void renderComponentText(AgriGuiWrapper gui, GuiComponent<String> component) {
		final FontRenderer fontRenderer = gui.getFontRenderer();
        
		String text = AgriCore.getTranslator().translate(component.getComponent());
		text = GuiHelper.splitInLines(gui.getFontRenderer(), text, 95, component.getScale());
		List<String> write = GuiHelper.getLinesFromData(text);
		for (int i = 0; i < write.size(); i++) {
			String line = write.get(i);
			int xOffset = component.isCentered() ? -fontRenderer.getStringWidth(line) / 2 : 0;
			int yOffset = i * fontRenderer.FONT_HEIGHT;
			//fontRenderer.drawString(line, (int) (this.bounds.x / scale) + xOffset, (int) (this.bounds.y / scale) + yOffset, 1644054);
			fontRenderer.drawString(line, xOffset, yOffset, 1644054);    //1644054 means black
		}
	}

}
