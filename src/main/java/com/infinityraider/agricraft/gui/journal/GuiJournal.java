package com.infinityraider.agricraft.gui.journal;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.util.MathHelper;
import com.infinityraider.agricraft.items.ItemJournal;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.gui.AgriGuiWrapper;
import com.infinityraider.agricraft.gui.ComponentGui;
import com.infinityraider.agricraft.gui.component.ComponentRenderer;
import com.infinityraider.agricraft.gui.component.GuiComponent;
import com.infinityraider.agricraft.gui.component.GuiComponentBuilder;

@SideOnly(Side.CLIENT)
public class GuiJournal extends ComponentGui {

    public static final int PAGE_WIDTH = 256;
    public static final int PAGE_HEIGHT = 192;

    public static final ResourceLocation LEFT_ARROW = new ResourceLocation("agricraft:textures/gui/journal/arrow_left.png");
    public static final ResourceLocation RIGHT_ARROW = new ResourceLocation("agricraft:textures/gui/journal/arrow_right.png");

    /**
     * Some dimensions and constants
     */
    private static final int MINIMUM_PAGES = 2;

    /**
     * Current page
     */
    private int currentPageNumber;
    private JournalPage currentPage;

    /**
     * Stuff to render
     */
    private final ItemStack journal;

    public GuiJournal(ItemStack journal) {
        super(PAGE_WIDTH, PAGE_HEIGHT, FAKE_CONTAINER);
        this.journal = journal;
    }

    @Override
    protected void onComponentGuiInit(AgriGuiWrapper wrapper) {
        this.clearComponents();
        this.clearBackgrounds();
        this.currentPage = getCurrentPage();
        this.addBackground(JournalPage.BACKGROUND);
        this.addBackground(this.currentPage.getForeground());
        this.addComponents(this.currentPage.getComponents());
        this.addNavArrows();
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

    private void addNavArrows() {
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
        this.addComponent(leftArrow);
        this.addComponent(rightArrow);
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
        this.onComponentGuiInit(null);
        return true;
    }

}
