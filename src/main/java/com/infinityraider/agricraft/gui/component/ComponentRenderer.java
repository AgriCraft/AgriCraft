/*
 */
package com.infinityraider.agricraft.gui.component;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.gui.AgriGuiWrapper;
import com.infinityraider.agricraft.utility.GuiHelper;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;

/**
 *
 * 
 */
public final class ComponentRenderer {
	
    public static final Color NORMAL_TEXT = new Color(14737632);
    public static final Color ACTIVE_TEXT = new Color(16777120);
    public static final Color DISABLED_TEXT = new Color(10526880);
    public static final ResourceLocation WIDGETS = new ResourceLocation("agricraft:textures/gui/widgets.png");
	public static final ResourceLocation FRAME_ICON = new ResourceLocation("agricraft:textures/gui/journal/GuiJournalSeedFrame.png");

	public static void renderIconComponent(AgriGuiWrapper gui, GuiComponent<ResourceLocation> component) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(component.getComponent());
		AgriGuiWrapper.drawModalRectWithCustomSizedTexture(
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
        GuiUtils.drawContinuousTexturedBox(WIDGETS, 0, 0, 0, 46 + getButtonNumber(component) * 20, component.getBounds().width, component.getBounds().height, 200, 20, 2, 3, 2, 2, 0);
        ComponentRenderer.renderComponentText(gui, component, getTextColor(component), true);
    }
    
    public static int getButtonNumber(GuiComponent component) {
        if (!component.isEnabled()) { 
            return 0;
        } else if (!component.isHovered()) {
            return 1;
        } else {
            return 2;
        }
    }
    
    public static Color getTextColor(GuiComponent component) {
        if (!component.isEnabled()) { 
            return DISABLED_TEXT;
        } else if (!component.isHovered()) {
            return NORMAL_TEXT;
        } else {
            return ACTIVE_TEXT;
        }
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
		AgriGuiWrapper.drawModalRectWithCustomSizedTexture(-1, -1, 0, 0, 18, 18, 18, 18);
	}
	
	public static void renderComponentText(AgriGuiWrapper gui, GuiComponent<String> component) {
		      renderComponentText(gui, component, Color.BLACK, false);
	}
    
    public static void renderComponentText(AgriGuiWrapper gui, GuiComponent<String> component, Color color, boolean shadow) {
		final FontRenderer fontRenderer = gui.getFontRenderer();
        
		String text = AgriCore.getTranslator().translate(component.getComponent());
		text = GuiHelper.splitInLines(gui.getFontRenderer(), text, 95, component.getScale());
		List<String> write = GuiHelper.getLinesFromData(text);
        float vertOffset = component.isCenteredVertically() ? (component.getBounds().height - write.size() * fontRenderer.FONT_HEIGHT) / 2f : 0;
		for (int i = 0; i < write.size(); i++) {
			String line = write.get(i);
			float xOffset = component.isCenteredHorizontally() ? (component.getBounds().width - fontRenderer.getStringWidth(line)) / 2f : 0;
			float yOffset = vertOffset + i * fontRenderer.FONT_HEIGHT;
			fontRenderer.drawString(line, xOffset, yOffset, color.hashCode(), shadow);
		}
	}

}
