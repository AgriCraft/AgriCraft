/*
 */
package com.infinityraider.agricraft.gui;

import com.infinityraider.agricraft.gui.component.GuiComponent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import scala.actors.threadpool.Arrays;

/*
 *
 */
public class ComponentGui<T extends Container> implements IAgriGui<T> {

    private final int width;
    private final int height;
    private List<GuiComponent> components;
    private List<ResourceLocation> backgrounds;
    private final T container;

    public ComponentGui(int width, int height, T container) {
        this.width = width;
        this.height = height;
        this.components = new ArrayList<>();
        this.backgrounds = new ArrayList<>();
        this.container = container;
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
        wrapper.resetPrevMousePos();
        this.onComponentGuiInit(wrapper);
    }

    protected void onComponentGuiInit(AgriGuiWrapper wrapper) {
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
                // Render Components
                .peek(c -> c.renderComponent(wrapper))
                // Render Bounds
                //.peek(c -> wrapper.drawRectangle(c.getBounds().x, c.getBounds().y, c.getBounds().width, c.getBounds().height, Color.BLACK.hashCode()))
                // Filter ToolTips
                .filter(c -> c.contains(relMouseX, relMouseY))
                // Add ToolTips
                .forEach(c -> c.addToolTip(tooltips, Minecraft.getMinecraft().thePlayer));
    }

    @Override
    public final synchronized void onMouseClicked(AgriGuiWrapper wrapper, int relMouseX, int relMouseY, int mouseButton) {
        this.components
                .stream()
                //.filter(c -> c.isEnabled())
                .filter(c -> c.contains(relMouseX, relMouseY))
                .anyMatch(c -> c.onClick(relMouseX, relMouseY, mouseButton));
    }

    @Override
    public final synchronized void onMouseMoved(AgriGuiWrapper wrapper, List<String> tooltips, int relMouseX, int relMouseY, int prevMouseX, int prevMouseY) {
        this.components
                .stream()
                //.filter(c -> c.isEnabled())
                .forEach(c -> c.onMouseMove(relMouseX, relMouseY));
    }

}
