package com.infinityraider.agricraft.gui.storage;

import com.infinityraider.agricraft.gui.component.Component;
import com.infinityraider.agricraft.container.ContainerSeedStorageBase;
import com.infinityraider.agricraft.blocks.tiles.storage.ISeedStorageControllable;
import com.infinityraider.agricraft.blocks.tiles.storage.SeedStorageSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.apiimpl.SeedRegistry;
import com.infinityraider.agricraft.gui.AgriGuiWrapper;
import com.infinityraider.agricraft.gui.ComponentGui;
import com.infinityraider.agricraft.gui.component.BasicComponents;
import com.infinityraider.agricraft.gui.component.GuiComponent;
import com.infinityraider.agricraft.gui.component.GuiComponentBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public abstract class GuiSeedStorageBase extends ComponentGui<ContainerSeedStorageBase> {

    // Button Dimensionss
    public static final int BUTTON_WIDTH = 60;
    public static final int BUTTON_HEIGHT = 12;

    //data for the active buttons
    protected AgriSeed activeSeed;
    private int scrollPositionVertical;
    private int scrollPositionHorizontal;
    private int sortStatId = -1;

    //other button stuff
    private final int maxVertSlots;
    private final int maxHorSlots;
    private final int sortButtonX;
    private final int sortButtonY;
    private final int setActiveSeedButtonOffset_X;
    private final int setActiveSeedButtonOffset_Y;
    private final int seedSlotButtonOffset_X;
    private final int seedSlotButtonOffset_Y;
    protected final List<Component<StorageElement>> activeSeeds = new ArrayList<>();
    protected List<GuiComponent<ItemStack>> setActiveSeedButtons;
    
    private String sortMethod = "growth";

    public GuiSeedStorageBase(ContainerSeedStorageBase container, int maxVertSlots, int maxHorSlots, int sortButtonX, int sortButtonY, int setActiveSeedButtonsX, int setActiveSeedButtonsY, int seedSlotsX, int seedSlotsY) {
        super(237, 131, container);
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
        TileEntity tile = this.getContainer().getTileEntity();
        if (tile instanceof ISeedStorageControllable) {
            this.activeSeed = ((ISeedStorageControllable) tile).getLockedSeed().orElse(null);
        }
    }

    @Override
    protected void onComponentGuiInit(AgriGuiWrapper wrapper) {
        this.clearComponents();
        
        this.addComponent(BasicComponents.getButtonComponent("agricraft_tooltip.growth", sortButtonX, calcSortY(0), BUTTON_WIDTH, BUTTON_HEIGHT, (c, p) -> setSortMethod("growth")));
        this.addComponent(BasicComponents.getButtonComponent("agricraft_tooltip.gain", sortButtonX, calcSortY(1), BUTTON_WIDTH, BUTTON_HEIGHT, (c, p) -> setSortMethod("gain")));
        this.addComponent(BasicComponents.getButtonComponent("agricraft_tooltip.strength", sortButtonX, calcSortY(2), BUTTON_WIDTH, BUTTON_HEIGHT, (c, p) -> setSortMethod("strength")));
        
        this.addComponent(BasicComponents.getButtonComponent("<<", sortButtonX, sortButtonY + 3 * (BUTTON_HEIGHT + 1), -1 + (BUTTON_WIDTH) / 4, BUTTON_HEIGHT, (c, p) -> this.scrollHorizontal(-this.getMaxHorizontalScroll())));
        this.addComponent(BasicComponents.getButtonComponent("<", sortButtonX + (BUTTON_WIDTH) / 4, sortButtonY + 3 * (BUTTON_HEIGHT + 1), -1 + (BUTTON_WIDTH) / 4, BUTTON_HEIGHT, (c, p) -> this.scrollHorizontal(-1)));
        this.addComponent(BasicComponents.getButtonComponent(">", sortButtonX + 1 + 2 * (BUTTON_WIDTH) / 4, sortButtonY + 3 * (BUTTON_HEIGHT + 1), -1 + (BUTTON_WIDTH) / 4, BUTTON_HEIGHT, (c, p) -> this.scrollHorizontal(1)));
        this.addComponent(BasicComponents.getButtonComponent(">>", sortButtonX + 1 + 3 * (BUTTON_WIDTH) / 4, sortButtonY + 3 * (BUTTON_HEIGHT + 1), -1 + (BUTTON_WIDTH) / 4, BUTTON_HEIGHT, (c, p) -> this.scrollHorizontal(this.getMaxHorizontalScroll())));
    }

    private int calcSortY(int index) {
        return sortButtonY + index + (index * BUTTON_HEIGHT);
    }
    
    private boolean setSortMethod(String method) {
        this.sortMethod = method;
        return true;
    }

    private void initSetActiveSeedButtons() {
        if (this.setActiveSeedButtonOffset_X < 0 || this.setActiveSeedButtonOffset_Y < 0) {
            return;
        }
        this.setActiveSeedButtons = new ArrayList<>();
        List<ItemStack> list = this.getContainer().getSeedEntries();
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
        this.activeSeeds.clear();
        List<SeedStorageSlot> list = this.getContainer().getSeedSlots(this.activeSeed);
        if (list != null) {
            this.sortByStat(list, sortMethod);
            for (int i = scrollPositionHorizontal; i < Math.min(list.size(), scrollPositionHorizontal + maxHorSlots); i++) {
                SeedStorageSlot slot = list.get(i);
                StorageElement stats = new StorageElement(slot.getId(), slot.count, slot.getSeed());
                activeSeeds.add(new Component<>(stats, seedSlotButtonOffset_X + (i - scrollPositionHorizontal) * 16, seedSlotButtonOffset_Y, 16, 16));
            }
        }
    }

    // TODO: Update method!
    private void sortByStat(List<SeedStorageSlot> list, String stat) {
        if (stat != null && this.activeSeed != null) {
            Collections.sort(list, new SeedStorageSlot.SlotComparator(stat));
        }
    }

    private boolean scrollVertical(int amount) {
        int newPos = this.scrollPositionVertical + amount;
        newPos = newPos < 0 ? 0 : newPos;
        int maxScrollY = this.getMaxVerticalScroll();
        newPos = newPos > maxScrollY ? maxScrollY : newPos;
        this.scrollPositionVertical = newPos;
        return true;
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

    private boolean scrollHorizontal(int amount) {
        if (this.hasActiveSeed()) {
            int newPos = this.scrollPositionHorizontal + amount;
            newPos = newPos < 0 ? 0 : newPos;
            int maxScrollX = this.getMaxHorizontalScroll();
            newPos = newPos > maxScrollX ? maxScrollX : newPos;
            this.scrollPositionHorizontal = newPos;
        }
        return true;
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
        return this.hasActiveSeed() ? this.getContainer().getSeedSlots(activeSeed).size() : 0;
    }

    protected void drawActiveEntries(AgriGuiWrapper wrapper, ResourceLocation texture, int xOffset, int yOffset) {
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
                ItemStack stack = this.activeSeed.toStack(element.amount);
                NBTTagCompound tag = new NBTTagCompound();
                element.getStat().writeToNBT(tag);
                stack.setTagCompound(tag);
                wrapper.getItemRender().renderItemIntoGUI(stack, component.xOffset(), component.yOffset());
                wrapper.getItemRender().renderItemOverlayIntoGUI(wrapper.getFontRenderer(), stack, component.xOffset(), component.yOffset(), "" + stack.stackSize);
                //draw the stat bars
                Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
                GL11.glDisable(GL11.GL_LIGHTING);
                // Unpack Stats
                short growth = element.getStat().getGrowth();
                short gain = element.getStat().getGain();
                short strength = element.getStat().getStrength();
                // Write Stats
                wrapper.drawTexturedModalRect(xOffset + (i * 16) + 1, yOffset - growth, 0, textureSize - growth, 3, growth);
                wrapper.drawTexturedModalRect(xOffset + i * 16 + 6, yOffset - gain, 0, textureSize - gain, 3, gain);
                wrapper.drawTexturedModalRect(xOffset + i * 16 + 11, yOffset - strength, 0, textureSize - strength, 3, strength);
                GL11.glEnable(GL11.GL_LIGHTING);
            }
        }
    }

    protected void drawScrollBarHorizontal(AgriGuiWrapper wrapper, ResourceLocation texture) {
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
        final int xOffset = 6;
        final int yOffset = 39;
        wrapper.drawTexturedModalRect(xOffset + offset, yOffset, 0, 135, 1, 5);
        wrapper.drawTexturedModalRect(xOffset + offset + 1, yOffset, 1, 135, length, 4);
        wrapper.drawTexturedModalRect(xOffset + offset + 1 + length, yOffset, total - 1, 135, 1, 5);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    protected void setActiveSeed(ItemStack stack) {
        SeedRegistry
                .getInstance()
                .valueOf(stack)
                .ifPresent(this::setActiveSeed);
    }

    protected void setActiveSeed(AgriSeed seed) {
        this.activeSeed = seed;
    }

    protected static class StorageElement {

        private final int id;
        private final int amount;
        private final AgriSeed seed;

        public StorageElement(int id, int amount, @Nonnull AgriSeed seed) {
            this.id = id;
            this.amount = amount;
            this.seed = seed;
        }

        public int id() {
            return id;
        }

        public int amount() {
            return amount;
        }

        public AgriSeed getSeed() {
            return seed;
        }

        public IAgriStat getStat() {
            return seed.getStat();
        }

        public IAgriPlant getPlant() {
            return seed.getPlant();
        }

    }

}
