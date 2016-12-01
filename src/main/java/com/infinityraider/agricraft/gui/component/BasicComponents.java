/*
 */
package com.infinityraider.agricraft.gui.component;

import com.agricraft.agricore.core.AgriCore;
import java.awt.Point;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 *
 * 
 */
public class BasicComponents {

	public static GuiComponent<String> getTextComponent(String string, int x, int y, double scale) {
		return new GuiComponentBuilder<>(string, x, y, 0, 0)
				.setScale(scale)
				.setRenderAction(ComponentRenderer::renderComponentText)
				.build();
	}

	public static GuiComponent<String> getTextComponent(String string, int x, int y, double scale, boolean centered) {
		return new GuiComponentBuilder<>(string, x, y, 0, 0)
				.setScale(scale)
				.setCentered(centered)
				.setRenderAction(ComponentRenderer::renderComponentText)
				.build();
	}
    
    public static GuiComponent<String> getButtonComponent(String string, int x, int y, int u, int v, BiFunction<GuiComponent<String>, Point, Boolean> onClick) {
		return new GuiComponentBuilder<>(AgriCore.getTranslator().translate(string), x, y, u, v)
				.setRenderAction(ComponentRenderer::renderComponentButton)
                .setMouseClickAction(onClick)
				.build();
	}

	public static GuiComponent<ItemStack> getStackComponent(ItemStack stack, int x, int y) {
		return new GuiComponentBuilder<>(stack, x, y, 16, 16)
				.setRenderAction(ComponentRenderer::renderComponentStack)
				.setTootipAdder(BasicComponents::addStackTooltip)
				.build();
	}

	public static GuiComponent<ItemStack> getStackComponentFramed(ItemStack stack, int x, int y) {
		return new GuiComponentBuilder<>(stack, x, y, 16, 16)
				.setRenderAction(ComponentRenderer::renderComponentStackFramed)
				.setTootipAdder(BasicComponents::addStackTooltip)
				.build();
	}

	public static GuiComponent<ResourceLocation> getIconComponent(ResourceLocation icon, int x, int y, int u, int v) {
		return new GuiComponentBuilder<>(icon, x, y, u, v)
				.setRenderAction(ComponentRenderer::renderIconComponent)
				.build();
	}

	public static GuiComponent<ResourceLocation> getIconComponent(ResourceLocation icon, int x, int y, int u, int v, String tooltip) {
		return new GuiComponentBuilder<>(icon, x, y, u, v)
				.setRenderAction(ComponentRenderer::renderIconComponent)
				.setTootipAdder((c, l) -> l.add(tooltip))
				.build();
	}

	public static void addStackTooltip(GuiComponent<ItemStack> component, List<String> tooltip) {
		if (component.getComponent() != null) {
			tooltip.addAll(component.getComponent().getTooltip(Minecraft.getMinecraft().thePlayer, false));
		}
	}

}
