/*
 */
package com.infinityraider.agricraft.gui;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.gui.component.GuiComponent;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import scala.actors.threadpool.Arrays;

/*
 *
 */
public class ComponentGui<T extends Container> implements IAgriGui<T> {

    @AgriConfigurable(key = "Enable GUI Inspector", category = AgriConfigCategory.DEBUG, comment = "Set to true to enable the GUI inspector for all AgriCraft GUIs.")
    public static boolean enableGuiInspector = false;

    private final int width;
    private final int height;
    private List<GuiComponent> components;
    private List<ResourceLocation> backgrounds;
    private final T container;
    private int lastMouseX;
    private int lastMouseY;

    public ComponentGui(int width, int height, T container) {
        this.width = width;
        this.height = height;
        this.components = new ArrayList<>();
        this.backgrounds = new ArrayList<>();
        this.container = container;
        this.lastMouseX = -1;
        this.lastMouseY = -1;
    }

    @Override
    public final int getHeight() {
        return this.height;
    }

    @Override
    public final int getWidth() {
        return this.width;
    }

    @Override
    public final T getContainer() {
        return this.container;
    }

    public final synchronized void resetMouse() {
        this.lastMouseX = -1;
        this.lastMouseY = -1;
    }

    public final synchronized List<GuiComponent> getComponents() {
        return new ArrayList<>(this.components);
    }

    public final synchronized List<ResourceLocation> getBackgrounds() {
        return new ArrayList<>(this.backgrounds);
    }

    public final synchronized boolean addComponent(GuiComponent component) {
        return this.components.add(component);
    }

    public final synchronized boolean addBackground(ResourceLocation background) {
        return this.backgrounds.add(background);
    }

    public final synchronized boolean addComponents(GuiComponent... components) {
        return this.components.addAll(Arrays.asList(components));
    }

    public final synchronized boolean addBackgrounds(ResourceLocation... backgrounds) {
        return this.backgrounds.addAll(Arrays.asList(backgrounds));
    }

    public final synchronized boolean addComponents(Collection<GuiComponent> components) {
        return this.components.addAll(components);
    }

    public final synchronized boolean addBackgrounds(Collection<ResourceLocation> backgrounds) {
        return this.backgrounds.addAll(backgrounds);
    }

    public final synchronized boolean removeComponent(GuiComponent component) {
        return this.components.remove(component);
    }

    public final synchronized boolean removeBackground(ResourceLocation background) {
        return this.backgrounds.remove(background);
    }

    public final synchronized void clearComponents() {
        this.components = new ArrayList<>();
    }

    public final synchronized void clearBackgrounds() {
        this.backgrounds = new ArrayList<>();
    }

    @Override
    public final synchronized void onGuiInit(AgriGuiWrapper wrapper) {
        this.resetMouse();
        this.onComponentGuiInit(wrapper);
    }

    protected synchronized void onComponentGuiInit(AgriGuiWrapper wrapper) {
        // Method hook.
    }

    @Override
    public final synchronized void onRenderBackground(AgriGuiWrapper wrapper, float f, int relMouseX, int relMouseY) {
        GlStateManager.pushAttrib();
        GlStateManager.color(1, 1, 1, 1);
        for (ResourceLocation r : backgrounds) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(r);
            wrapper.drawTexturedModalRect(0, 0, 0, 0, this.width, this.height);
        }
        GlStateManager.popAttrib();
    }

    @Override
    public final synchronized void onRenderForeground(AgriGuiWrapper wrapper, List<String> tooltips, int relMouseX, int relMouseY) {
        // Render all components.
        this.components.stream()
                // Filter Visable.
                .filter(c -> c.isVisable())
                // Call Render Hook
                .peek(c -> onRenderComponent(wrapper, c, tooltips, relMouseX, relMouseY))
                // Filter ToolTips
                .filter(c -> c.contains(relMouseX, relMouseY))
                // Add ToolTips
                .forEach(c -> onAddComponentToolTip(wrapper, c, tooltips, relMouseX, relMouseY));
        // If inspector mode enabled, render the inspector.
        if (enableGuiInspector) {
            onRenderInspector(wrapper, tooltips, relMouseX, relMouseY);
        }
    }

    public final synchronized void onRenderComponent(AgriGuiWrapper wrapper, GuiComponent c, List<String> tooltips, int relMouseX, int relMouseY) {
        // Render the component.
        c.renderComponent(wrapper);

        // If inspector mode is on, render the component bounds.
        if (enableGuiInspector) {
            wrapper.drawRectangle(c.getBounds().x, c.getBounds().y, c.getBounds().width, c.getBounds().height, Color.MAGENTA.hashCode());
        }
    }

    public final synchronized void onAddComponentToolTip(AgriGuiWrapper wrapper, GuiComponent c, List<String> tooltips, int relMouseX, int relMouseY) {
        // Add the component tool tip.
        c.addToolTip(tooltips, wrapper.mc.player);
    }

    public final synchronized void onRenderInspector(AgriGuiWrapper wrapper, List<String> tooltips, int relMouseX, int relMouseY) {
        // Add the window information.
        tooltips.add(ChatFormatting.DARK_AQUA + "Window:");
        tooltips.add(ChatFormatting.DARK_AQUA + " - ID: " + this.container.windowId);
        tooltips.add(ChatFormatting.DARK_AQUA + " - Dimensions: (" + wrapper.width + ", " + wrapper.height + ")");
        tooltips.add(ChatFormatting.DARK_AQUA + " - Pausing: " + wrapper.doesGuiPauseGame());

        // Add the GUI information.
        tooltips.add(ChatFormatting.DARK_AQUA + "GUI:");
        tooltips.add(ChatFormatting.DARK_AQUA + " - Dimensions: (" + this.width + ", " + this.height + ")");
        tooltips.add(ChatFormatting.DARK_AQUA + " - Components: " + this.components.size());
        tooltips.add(ChatFormatting.DARK_AQUA + " - Backgrounds: " + this.backgrounds.size());
        tooltips.add(ChatFormatting.DARK_AQUA + " - ItemStacks: " + this.container.inventoryItemStacks.size());
        tooltips.add(ChatFormatting.DARK_AQUA + " - Item Slots: " + this.container.inventorySlots.size());

        // Add the mouse Information.
        tooltips.add(ChatFormatting.DARK_AQUA + "Mouse:");
        tooltips.add(ChatFormatting.DARK_AQUA + " - Position   : (" + relMouseX + ", " + relMouseY + ")");

        // Add the hovered component information.
        tooltips.add(ChatFormatting.DARK_AQUA + "Hovered:");

        // Fetch the component being hovered over.
        Optional<GuiComponent> optionalHovered = Optional.empty();

        // Iterate through in reverse.
        for (int i = this.components.size() - 1; i > -1; i--) {
            if (this.components.get(i).contains(relMouseX, relMouseY)) {
                optionalHovered = Optional.of(this.components.get(i));
                break;
            }
        }

        // If hovering over something add it to the tooltip.
        if (optionalHovered.isPresent()) {
            // Unwrap the hovered component.
            GuiComponent hovered = optionalHovered.get();
            // Draw highlighted bounds.
            wrapper.drawRectangle(hovered.getBounds().x, hovered.getBounds().y, hovered.getBounds().width, hovered.getBounds().height, Color.PINK.hashCode());
            // Add information to the tooltip.
            tooltips.add(ChatFormatting.DARK_AQUA + " - Component: " + Objects.toString(hovered.getComponent()));
            tooltips.add(ChatFormatting.DARK_AQUA + " - Position: (" + hovered.getBounds().x + ", " + hovered.getBounds().y + ")");
            tooltips.add(ChatFormatting.DARK_AQUA + " - Dimensions: (" + hovered.getBounds().width + ", " + hovered.getBounds().height + ")");
            tooltips.add(ChatFormatting.DARK_AQUA + " - Enabled: " + hovered.isEnabled());
            tooltips.add(ChatFormatting.DARK_AQUA + " - Visable: " + hovered.isVisable());
            tooltips.add(ChatFormatting.DARK_AQUA + " - Hovered: " + hovered.isHovered());
        } else {
            tooltips.add(ChatFormatting.DARK_AQUA + " - None");
        }
    }

    @Override
    public final synchronized void onMouseClicked(AgriGuiWrapper wrapper, int relMouseX, int relMouseY, int mouseButton) {
        this.components
                .stream()
                .filter(c -> c.isEnabled())
                .filter(c -> c.contains(relMouseX, relMouseY))
                .anyMatch(c -> c.onClick(relMouseX, relMouseY, mouseButton));
    }

    @Override
    public final synchronized void onUpdateMouse(AgriGuiWrapper wrapper, List<String> tooltips, int relMouseX, int relMouseY) {
        if (this.lastMouseX != relMouseX || this.lastMouseY != relMouseY) {
            this.components
                    .stream()
                    .filter(c -> c.isEnabled())
                    .forEach(c -> c.onMouseMove(relMouseX, relMouseY));
            this.lastMouseX = relMouseX;
            this.lastMouseY = relMouseY;
        }
    }
    
    static {
        AgriCore.getConfig().addConfigurable(ComponentGui.class);
    }

}
