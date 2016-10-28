package com.infinityraider.agricraft.gui.journal;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.agricraft.agricore.util.MathHelper;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.gui.GuiBase;
import com.infinityraider.agricraft.gui.component.ComponentRenderer;
import com.infinityraider.agricraft.gui.component.GuiComponent;
import com.infinityraider.agricraft.gui.component.GuiComponentBuilder;
import com.infinityraider.agricraft.items.ItemJournal;

@SideOnly(Side.CLIENT)
public class GuiJournal extends GuiBase {

    public static final ResourceLocation LEFT_ARROW = new ResourceLocation("agricraft:textures/gui/journal/arrow_left.png");
    public static final ResourceLocation RIGHT_ARROW = new ResourceLocation("agricraft:textures/gui/journal/arrow_right.png");

    /**
     * Some dimensions and constants
     */
    private static final int MINIMUM_PAGES = 2;

    private final int xSize;
    private final int ySize;

    private int guiLeft;
    private int guiTop;

    /**
     * Current page
     */
    private int currentPageNumber;
    private JournalPage currentPage;

    /**
     * Stuff to render
     */
    List<GuiComponent> components = new ArrayList<>();

    private final ItemStack journal;

    public GuiJournal(ItemStack journal) {
        super();
        this.journal = journal;
        int pageWidth = 128;
        this.xSize = pageWidth * 2;
        this.ySize = pageWidth * 3 / 2;
    }

    @Override
    public void initGui() {
        //half of the screen size minus the gui size to centre the gui, the -16 is to ignore the players item bar
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - 16 - this.ySize) / 2;
        currentPage = getCurrentPage();
        this.components = new ArrayList<>();
        addNavArrows(components);
        currentPage.addComponents(components);
    }

    @Override
    public int getAnchorX() {
        return this.guiLeft;
    }

    @Override
    public int getAnchorY() {
        return this.guiTop;
    }

    @Override
    public void drawScreen(int x, int y, float opacity) {
        //draw background
        drawBackground(0);
        //draw foreground
        drawTexture(currentPage.getForeground());
        //draw tooltip
        final int mouseX = x - this.guiLeft;
        final int mouseY = y - this.guiTop;
        // Update Mouse
        this.components.forEach(c -> c.onMouseMove(mouseX, mouseY));
        List<String> toolTip = new ArrayList<>();
        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.guiLeft, this.guiTop, 0);
        components.stream()
                // Render Components
                .peek(c -> c.renderComponent(this))
                // Filter ToolTips
                .filter(c -> c.contains(mouseX, mouseY))
                // Add ToolTips
                .forEach(c -> c.addToolTip(toolTip, Minecraft.getMinecraft().thePlayer));
        currentPage.addTooltip(mouseX, mouseY, toolTip);
        drawHoveringText(toolTip, mouseX, mouseY, fontRendererObj);
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    @Override
    public void mouseClicked(int x, int y, int rightClick) {
        final int mouseX = x - this.guiLeft;
        final int mouseY = y - this.guiTop;
        this.components.stream()
                .filter(c -> c.contains(mouseX, mouseY))
                .anyMatch(c -> c.onClick(mouseX, mouseY));
    }

    private JournalPage getCurrentPage() {
        switch (currentPageNumber) {
            case 0:
                return new JournalPageTitle();
            case 1:
                return new JournalPageIntroduction();
        }
        return new JournalPageSeed(this, getDiscoveredSeeds(), currentPageNumber - MINIMUM_PAGES);
    }

    private List<IAgriPlant> getDiscoveredSeeds() {
        if (journal != null && journal.getItem() instanceof ItemJournal) {
            return ((ItemJournal) journal.getItem()).getDiscoveredSeeds(journal);
        } else {
            return new ArrayList<>();
        }
    }

    private int getNumberOfPages() {
        return MINIMUM_PAGES + getDiscoveredSeeds().size();
    }

    @Override
    public void drawBackground(int i) {
        this.drawTexture(JournalPage.getBackground());
    }

    private void drawTexture(ResourceLocation texture) {
        GlStateManager.pushAttrib();
        GlStateManager.color(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        GlStateManager.popAttrib();
    }

    private void addNavArrows(List<GuiComponent> components) {
        GuiComponent leftArrow = new GuiComponentBuilder<>(LEFT_ARROW, 1, 170, 32, 32)
                .setRenderAction(ComponentRenderer::renderIconComponent)
                .setMouseEnterAction((c, p) -> c.setVisable(this.currentPageNumber > 0))
                .setMouseLeaveAction((c, p) -> c.setVisable(false))
                .setMouseClickAction((c, p) -> incPage(-1))
                .setVisable(false)
                .build();
        GuiComponent rightArrow = new GuiComponentBuilder<>(RIGHT_ARROW, 223, 170, 32, 32)
                .setRenderAction(ComponentRenderer::renderIconComponent)
                .setMouseEnterAction((c, p) -> c.setVisable(this.currentPageNumber < this.getNumberOfPages() - 1))
                .setMouseLeaveAction((c, p) -> c.setVisable(false))
                .setMouseClickAction((c, p) -> incPage(1))
                .setVisable(false)
                .build();
        components.add(leftArrow);
        components.add(rightArrow);
    }

    public boolean switchPage(IAgriPlant plant) {
        final int page = this.getDiscoveredSeeds().indexOf(plant) + 2;
        return page != -1 && setPage(page);
    }

    public boolean incPage(int inc) {
        return this.setPage(this.currentPageNumber + inc);
    }

    public boolean setPage(int page) {
        this.currentPageNumber = MathHelper.inRange(page, 0, this.getNumberOfPages() - 1);
        this.initGui();
        return true;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
