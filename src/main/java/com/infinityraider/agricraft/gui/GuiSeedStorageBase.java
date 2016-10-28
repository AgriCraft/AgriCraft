package com.infinityraider.agricraft.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.apiimpl.StatRegistry;
import com.infinityraider.agricraft.blocks.tiles.storage.ISeedStorageControllable;
import com.infinityraider.agricraft.blocks.tiles.storage.SeedStorageSlot;
import com.infinityraider.agricraft.container.ContainerSeedStorageBase;
import com.infinityraider.agricraft.gui.component.Component;
import com.infinityraider.agricraft.gui.component.GuiComponent;
import com.infinityraider.agricraft.gui.component.GuiComponentBuilder;
import com.infinityraider.agricraft.utility.StackHelper;

import com.agricraft.agricore.core.AgriCore;

@SideOnly(Side.CLIENT)
public abstract class GuiSeedStorageBase extends GuiContainer {
    //the container for this gui

    public ContainerSeedStorageBase container;
    //data for the active buttons
    Item activeSeed;
    int activeMeta;
    private int scrollPositionVertical;
    private int scrollPositionHorizontal;
    private int sortStatId = -1;
    //button ID constants
    protected static final int buttonIdGrowth = 0;
    protected static final int buttonIdGain = 1;
    protected static final int buttonIdStrength = 2;
    protected static final int buttonIdScrollDown = 3;
    protected static final int buttonIdScrollUp = 4;
    protected static final int buttonIdScrollLeft = 5;
    protected static final int buttonIdScrollRight = 6;
    protected static final int buttonIdDownEnd = 7;
    protected static final int buttonIdUpEnd = 8;
    protected static final int buttonIdLeftEnd = 9;
    protected static final int buttonIdRightEnd = 10;
    //other button stuff
    private final int maxVertSlots;
    private final int maxHorSlots;
    private final int sortButtonX;
    private final int sortButtonY;
    private final int setActiveSeedButtonOffset_X;
    private final int setActiveSeedButtonOffset_Y;
    private final int seedSlotButtonOffset_X;
    private final int seedSlotButtonOffset_Y;
    protected List<Component<StorageElement>> activeSeeds;
    protected List<GuiComponent<ItemStack>> setActiveSeedButtons;

    public GuiSeedStorageBase(ContainerSeedStorageBase container, int maxVertSlots, int maxHorSlots, int sortButtonX, int sortButtonY, int setActiveSeedButtonsX, int setActiveSeedButtonsY, int seedSlotsX, int seedSlotsY) {
        super(container);
        this.container = container;
        this.maxVertSlots = maxVertSlots;
        this.maxHorSlots = maxHorSlots;
        this.sortButtonX = sortButtonX;
        this.sortButtonY = sortButtonY;
        this.setActiveSeedButtonOffset_X = setActiveSeedButtonsX;
        this.setActiveSeedButtonOffset_Y = setActiveSeedButtonsY;
        this.seedSlotButtonOffset_X = seedSlotsX;
        this.seedSlotButtonOffset_Y = seedSlotsY;
    }

    protected boolean hasActiveSeed() {
        return this.activeSeed != null;
    }

    protected void getActiveSeed() {
        TileEntity tile = this.container.getTileEntity();
        if (tile == null || !(tile instanceof ISeedStorageControllable)) {
            return;
        }
        ISeedStorageControllable storage = (ISeedStorageControllable) tile;
        ItemStack stack = storage.getLockedSeed();
        if (stack != null && stack.getItem() != null) {
            activeSeed = stack.getItem();
            activeMeta = stack.getItemDamage();
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        loadButtons();
    }

    @SuppressWarnings("unchecked")
    protected void loadButtons() {
        this.buttonList.clear();
        int buttonWidth = 60;
        int buttonHeight = 12;
        this.buttonList.add(new GuiButton(buttonIdGrowth, this.guiLeft + sortButtonX, this.guiTop + sortButtonY, buttonWidth, buttonHeight, AgriCore.getTranslator().translate("agricraft_tooltip.growth")));
        this.buttonList.add(new GuiButton(buttonIdGain, this.guiLeft + sortButtonX, this.guiTop + sortButtonY + buttonHeight + 1, buttonWidth, buttonHeight, AgriCore.getTranslator().translate("agricraft_tooltip.gain")));
        this.buttonList.add(new GuiButton(buttonIdStrength, this.guiLeft + sortButtonX, this.guiTop + sortButtonY + 2 * (buttonHeight + 1), buttonWidth, buttonHeight, AgriCore.getTranslator().translate("agricraft_tooltip.strength")));
        this.buttonList.add(new GuiButton(buttonIdLeftEnd, this.guiLeft + sortButtonX, this.guiTop + sortButtonY + 3 * (buttonHeight + 1), -1 + (buttonWidth) / 4, buttonHeight, "<<"));
        this.buttonList.add(new GuiButton(buttonIdScrollLeft, this.guiLeft + sortButtonX + (buttonWidth) / 4, this.guiTop + sortButtonY + 3 * (buttonHeight + 1), -1 + (buttonWidth) / 4, buttonHeight, "<"));
        this.buttonList.add(new GuiButton(buttonIdScrollRight, this.guiLeft + sortButtonX + 1 + 2 * (buttonWidth) / 4, this.guiTop + sortButtonY + 3 * (buttonHeight + 1), -1 + (buttonWidth) / 4, buttonHeight, ">"));
        this.buttonList.add(new GuiButton(buttonIdRightEnd, this.guiLeft + sortButtonX + 1 + 3 * (buttonWidth) / 4, this.guiTop + sortButtonY + 3 * (buttonHeight + 1), -1 + (buttonWidth) / 4, buttonHeight, ">>"));
    }

    private void initSetActiveSeedButtons() {
        if (this.setActiveSeedButtonOffset_X < 0 || this.setActiveSeedButtonOffset_Y < 0) {
            return;
        }
        this.setActiveSeedButtons = new ArrayList<>();
        List<ItemStack> list = container.getSeedEntries();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                int xOffset = this.setActiveSeedButtonOffset_X + (16 * i) % 64;
                int yOffset = this.setActiveSeedButtonOffset_Y + 16 * (i / 4);
                this.setActiveSeedButtons.add(new GuiComponentBuilder<>(list.get(i), xOffset, yOffset, 16, 16).build());
            }
        }
    }

    private void initSeedSlots() {
        getActiveSeed();
        this.activeSeeds = new ArrayList<>();
        List<SeedStorageSlot> list = this.container.getSeedSlots(this.activeSeed, this.activeMeta);
        if (list != null) {
            this.sortByStat(list);
            for (int i = scrollPositionHorizontal; i < Math.min(list.size(), scrollPositionHorizontal + maxHorSlots); i++) {
                SeedStorageSlot slot = list.get(i);
                StorageElement stats = new StorageElement(slot.getId(), slot.getStack(this.activeSeed, this.activeMeta));
                activeSeeds.add(new Component<>(stats, this.guiLeft + seedSlotButtonOffset_X + (i - scrollPositionHorizontal) * 16, this.guiTop + seedSlotButtonOffset_Y, 16, 16));
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id <= buttonIdStrength && this.activeSeed != null) {
            this.sortStatId = button.id;
        } else {
            switch (button.id) {
                case buttonIdScrollDown:
                    this.scrollVertical(-1);
                    break;
                case buttonIdScrollUp:
                    this.scrollVertical(1);
                    break;
                case buttonIdScrollRight:
                    this.scrollHorizontal(1);
                    break;
                case buttonIdScrollLeft:
                    this.scrollHorizontal(-1);
                    break;
                case buttonIdDownEnd:
                    this.scrollVertical(this.getMaxVerticalScroll());
                    break;
                case buttonIdUpEnd:
                    this.scrollVertical(-this.getMaxVerticalScroll());
                    break;
                case buttonIdLeftEnd:
                    this.scrollHorizontal(-this.getMaxHorizontalScroll());
                    break;
                case buttonIdRightEnd:
                    this.scrollHorizontal(this.getMaxHorizontalScroll());
                    break;
            }
        }
        this.updateScreen();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.initSetActiveSeedButtons();
        this.initSeedSlots();
    }

    @Override
    protected void mouseClicked(int x, int y, int rightClick) throws IOException {
        //set active SEED button clicked
        if (this.setActiveSeedButtons != null) {
            for (GuiComponent<ItemStack> component : setActiveSeedButtons) {
                if (component.contains(guiLeft + x, guiTop + y)) {
                    this.setActiveSeed(component.getComponent());
                    return;
                }
            }
        }
        //click to get SEED out of the storage
        if (this.hasActiveSeed()) {
            for (Component<StorageElement> component : activeSeeds) {
                if (component.isOverComponent(x, y)) {
                    StorageElement stats = component.getComponent();
                    int stackSize = isShiftKeyDown() ? 64 : 1;
                    this.container.moveStackFromTileEntityToPlayer(stats.id, new ItemStack(activeSeed, stackSize, activeMeta));
                    this.updateScreen();
                    return;
                }
            }
        }
        super.mouseClicked(x, y, rightClick);
    }

    // TODO: Update method!
    private void sortByStat(List<SeedStorageSlot> list) {
        String stat = null;
        switch (this.sortStatId) {
            case buttonIdGrowth:
                stat = "growth";
                break;
            case buttonIdGain:
                stat = "gain";
                break;
            case buttonIdStrength:
                stat = "strength";
                break;
        }
        if (stat != null && this.activeSeed != null) {
            Collections.sort(list, new SeedStorageSlot.SlotComparator(stat));
        }
    }

    private void scrollVertical(int amount) {
        int newPos = this.scrollPositionVertical + amount;
        newPos = newPos < 0 ? 0 : newPos;
        int maxScrollY = this.getMaxVerticalScroll();
        newPos = newPos > maxScrollY ? maxScrollY : newPos;
        this.scrollPositionVertical = newPos;
    }

    private int getMaxVerticalScroll() {
        int nrSeedButtons = this.setActiveSeedButtons.size();
        int nrRows = (nrSeedButtons % 4 > 0 ? 1 : 0) + nrSeedButtons / 4;
        if (nrRows <= this.maxVertSlots) {
            return 0;
        } else {
            return nrRows - maxVertSlots;
        }
    }

    private void scrollHorizontal(int amount) {
        if (this.hasActiveSeed()) {
            int newPos = this.scrollPositionHorizontal + amount;
            newPos = newPos < 0 ? 0 : newPos;
            int maxScrollX = this.getMaxHorizontalScroll();
            newPos = newPos > maxScrollX ? maxScrollX : newPos;
            this.scrollPositionHorizontal = newPos;
        }
    }

    private int getMaxHorizontalScroll() {
        int nrSlots = seedSlotAmount();
        if (nrSlots <= maxHorSlots) {
            return 0;
        } else {
            return nrSlots - maxHorSlots;
        }
    }

    private int seedSlotAmount() {
        return this.hasActiveSeed() ? this.container.getSeedSlots(activeSeed, activeMeta).size() : 0;
    }

    protected void drawActiveEntries(ResourceLocation texture, int xOffset, int yOffset) {
        if (!this.hasActiveSeed()) {
            return;
        }
        int textureSize = 256;
        GL11.glColor4f(1F, 1F, 1F, 1F);
        for (int i = 0; i < this.activeSeeds.size(); i++) {
            Component<StorageElement> component = activeSeeds.get(i);
            if (component != null && component.getComponent() != null) {
                StorageElement element = component.getComponent();
                //draw the SEED icon
                ItemStack stack = new ItemStack(activeSeed, element.amount, activeMeta);
                NBTTagCompound tag = new NBTTagCompound();
                element.getStat().writeToNBT(tag);
                stack.setTagCompound(tag);
                itemRender.renderItemIntoGUI(stack, component.xOffset(), component.yOffset());
                itemRender.renderItemOverlayIntoGUI(fontRendererObj, stack, component.xOffset(), component.yOffset(), "" + stack.stackSize);
                //draw the stat bars
                Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
                GL11.glDisable(GL11.GL_LIGHTING);
                // Unpack Stats
                short growth = element.getStat().getGrowth();
                short gain = element.getStat().getGain();
                short strength = element.getStat().getStrength();
                // Write Stats
                this.drawTexturedModalRect(this.guiLeft + xOffset + (i * 16) + 1, this.guiTop + yOffset - growth, 0, textureSize - growth, 3, growth);
                this.drawTexturedModalRect(this.guiLeft + xOffset + i * 16 + 6, this.guiTop + yOffset - gain, 0, textureSize - gain, 3, gain);
                this.drawTexturedModalRect(this.guiLeft + xOffset + i * 16 + 11, this.guiTop + yOffset - strength, 0, textureSize - strength, 3, strength);
                GL11.glEnable(GL11.GL_LIGHTING);
            }
        }
    }

    protected void drawScrollBarHorizontal(ResourceLocation texture) {
        int nrSlots = seedSlotAmount();
        int total = 224;
        int slotWidth = 16;
        int fullLength = nrSlots <= maxHorSlots ? total : slotWidth * nrSlots;
        float unit = ((float) slotWidth) / ((float) fullLength) * total;
        int offset = (int) (scrollPositionHorizontal * unit);
        int length = (int) (maxHorSlots * unit);
        length = length <= 2 ? 0 : length >= total ? total - 2 : length;
        offset = offset == 0 ? offset : offset - 1;

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glDisable(GL11.GL_LIGHTING);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        int xOffset = this.guiLeft + 6;
        int yOffset = this.guiTop + 39;
        this.drawTexturedModalRect(xOffset + offset, yOffset, 0, 135, 1, 5);
        this.drawTexturedModalRect(xOffset + offset + 1, yOffset, 1, 135, length, 4);
        this.drawTexturedModalRect(xOffset + offset + 1 + length, yOffset, total - 1, 135, 1, 5);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @SuppressWarnings("unchecked")
    protected void drawTooltip(int x, int y) {
        if (!this.hasActiveSeed()) {
            return;
        }
        for (Component<StorageElement> component : this.activeSeeds) {
            if (component != null && component.getComponent() != null) {
                //tooltip
                if (component.isOverComponent(x, y)) {
                    StorageElement element = component.getComponent();
                    ItemStack stack = new ItemStack(activeSeed, element.amount, activeMeta);
                    NBTTagCompound tag = new NBTTagCompound();
                    element.getStat().writeToNBT(tag);
                    stack.setTagCompound(tag);
                    List toolTip = stack.getTooltip(Minecraft.getMinecraft().thePlayer, true);
                    drawHoveringText(toolTip, x - this.guiLeft, y - this.guiTop, fontRendererObj);
                }
            }
        }
    }

    protected void setActiveSeed(ItemStack stack) {
        this.activeSeed = stack.getItem();
        this.activeMeta = stack.getItemDamage();
    }

    //opening the gui doesn't pause the game
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    protected static class StorageElement {

        private final IAgriStat stats;
        private final int id;
        private final int amount;

        public StorageElement(int id, @Nonnull ItemStack stack) {
            this.stats = StatRegistry.getInstance().valueOf(StackHelper.getTag(stack)).get();
            this.amount = stack.stackSize;
            this.id = id;
        }

        public int id() {
            return id;
        }

        public int amount() {
            return amount;
        }

        public IAgriStat getStat() {
            return stats;
        }

    }

}
