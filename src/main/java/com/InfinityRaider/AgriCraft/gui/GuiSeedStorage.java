package com.InfinityRaider.AgriCraft.gui;

import com.InfinityRaider.AgriCraft.container.ContainerSeedStorage;
import com.InfinityRaider.AgriCraft.container.SlotSeedStorage;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedStorage;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GuiSeedStorage extends GuiContainer {
    public static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/GuiSeedStorage.png");
    public HashMap<ItemSeeds, HashMap<Integer, ArrayList<SlotSeedStorage>>> entries;
    private ItemStack activeEntry;

    private int scrollPositionVertical;
    private int scrollPositionHorizontal;
    private static int maxNrVerticalSeeds = 10;
    private static int maxNrHorizontalSeeds = 6;

    private static final int buttonIdGrowth = 0;
    private static final int buttonIdGain = 1;
    private static final int buttonIdStrength = 2;
    private static final int buttonIdScrollDown = 3;
    private static final int buttonIdScrollUp = 4;
    private static final int buttonIdScrollLeft = 5;
    private static final int buttonIdScrollRight = 6;


    public GuiSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(new ContainerSeedStorage(inventory, te));
        this.xSize = 250;
        this.ySize = 176;
        this.setEntries();
    }

    public void setEntries() {
        this.entries = new HashMap<ItemSeeds, HashMap<Integer, ArrayList<SlotSeedStorage>>>();
        for(SlotSeedStorage slot:((ContainerSeedStorage) this.inventorySlots).getSeedSlots()) {
            this.addSlotToEntries(slot);
        }
    }

    //puts an existing slot from the container in the gui map
    public void addSlotToEntries(SlotSeedStorage slot) {
        if(slot!=null && slot.getStack()!=null && slot.getStack().getItem() instanceof ItemSeeds) {
            ItemStack stack = slot.getStack();
            ItemSeeds seed = (ItemSeeds) stack.getItem();
            int seedMeta = stack.getItemDamage();
            HashMap<Integer, ArrayList<SlotSeedStorage>> itemEntry = entries.get(seed);
            //there is already an entry for this item
            if(itemEntry != null) {
                ArrayList<SlotSeedStorage> metaEntry = itemEntry.get(seedMeta);
                //there is already an entry for this item & meta
                if(metaEntry != null) {
                    metaEntry.add(slot);
                }
                //there is no entry for this meta
                else {
                    ArrayList<SlotSeedStorage> newSlotList = new ArrayList<SlotSeedStorage>();
                    newSlotList.add(slot);
                    itemEntry.put(seedMeta, newSlotList);
                }
            }
            //there is no entry for this seed yet
            else {
                ArrayList<SlotSeedStorage> newSlotList = new ArrayList<SlotSeedStorage>();
                newSlotList.add(slot);
                HashMap<Integer, ArrayList<SlotSeedStorage>> newMetaEntry = new HashMap<Integer, ArrayList<SlotSeedStorage>>();
                newMetaEntry.put(seedMeta, newSlotList);
                this.entries.put(seed, newMetaEntry);
            }
        }
    }

    //adds an itemstack to the container
    public void addEntry(ItemStack stack) {
        ContainerSeedStorage container = (ContainerSeedStorage) this.inventorySlots;
        if(stack!=null && stack.getItem()!=null && stack.getItem() instanceof ItemSeeds) {
            HashMap<Integer, ArrayList<SlotSeedStorage>> itemEntry = entries.get(stack.getItem());
            //there is an entry for this item
            if(itemEntry !=null) {
                ArrayList<SlotSeedStorage> seedEntries = itemEntry.get(stack.getItemDamage());
                //there is an entry for this item and meta
                if(seedEntries!=null) {
                    boolean seedAdded = false;
                    for(SlotSeedStorage slot:seedEntries) {
                        //there is an entry with equal NBT
                        ItemStack seedStack = slot.getStack();
                        if(ItemStack.areItemStackTagsEqual(seedStack, stack)) {
                            slot.count = slot.count + stack.stackSize;
                            seedAdded = true;
                        }
                    }
                    //there is no entry with equal NBT
                    if(!seedAdded) {
                        seedEntries.add(container.addNewSlot(stack));
                    }
                }
                //there is not yet an entry for this  meta
                else {
                    ArrayList<SlotSeedStorage> newList = new ArrayList<SlotSeedStorage>();
                    newList.add(container.addNewSlot(stack));
                    itemEntry.put(stack.getItemDamage(), newList);
                }
            }
            //there is no entry for this item yet
            else {
                ArrayList<SlotSeedStorage> newList = new ArrayList<SlotSeedStorage>();
                newList.add(container.addNewSlot(stack));
                HashMap<Integer, ArrayList<SlotSeedStorage>> newEntry = new HashMap<Integer, ArrayList<SlotSeedStorage>>();
                newEntry.put(stack.getItemDamage(), newList);
                entries.put((ItemSeeds) stack.getItem(), newEntry);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.id<=buttonIdStrength) {
            this.sortByStat(button.id);
        }
        else if(button.id<=buttonIdScrollRight) {
            switch(button.id) {
                case buttonIdScrollDown: this.scrollPositionVertical = this.scrollPositionVertical +1; break;
                case buttonIdScrollUp: this.scrollPositionVertical = this.scrollPositionVertical +1; break;
                case buttonIdScrollRight: this.scrollPositionHorizontal = this.scrollPositionHorizontal +1; break;
                case buttonIdScrollLeft: this.scrollPositionHorizontal = this.scrollPositionHorizontal-1; break;
            }
        }
        else {
            this.setActiveEntry(button.id-1-buttonIdScrollRight);
        }
        this.updateScreen();
    }

    protected void sortByStat(int id) {
        String stat=null;
        switch(id) {
            case buttonIdGrowth: stat = Names.NBT.growth; break;
            case buttonIdGain: stat = Names.NBT.gain; break;
            case buttonIdStrength: stat = Names.NBT.strength; break;
        }
        if(stat!=null) {
            ArrayList<SlotSeedStorage> activeEntries = this.getActiveEntries();

        }
    }

    //returns an array list of all the kinds of seeds that are in the storage
    public ArrayList<ItemStack> getSeedStacks() {
        ArrayList<ItemStack> seedStacks = new ArrayList<ItemStack>();
        for(Map.Entry<ItemSeeds, HashMap<Integer, ArrayList<SlotSeedStorage>>> seedItemEntry:this.entries.entrySet()) {
            for(Map.Entry<Integer, ArrayList<SlotSeedStorage>> seedMetaEntry:seedItemEntry.getValue().entrySet()) {
                seedStacks.add(new ItemStack(seedItemEntry.getKey(), seedMetaEntry.getKey()));
            }
        }
        return  seedStacks;
    }

    //returns an array list of all the slots holding seeds of this active seeds
    public ArrayList<SlotSeedStorage> getActiveEntries() {
        ArrayList<SlotSeedStorage> seeds = new ArrayList<SlotSeedStorage>();
        for(Map.Entry<ItemSeeds, HashMap<Integer, ArrayList<SlotSeedStorage>>> seedItemEntry:this.entries.entrySet()) {
            if(this.activeEntry.getItem()==seedItemEntry.getKey()) {
                for(Map.Entry<Integer, ArrayList<SlotSeedStorage>> seedMetaEntry:seedItemEntry.getValue().entrySet()) {
                    if(seedMetaEntry.getKey()==this.activeEntry.getItemDamage()) {
                        seeds = seedMetaEntry.getValue();
                        break;
                    }
                }
                break;
            }
        }
        return seeds;
    }

    protected void setActiveEntry(int i) {
        this.activeEntry = this.getSeedStacks().get(i);
        this.scrollPositionHorizontal = 0;
        this.updateScreen();
    }

    //draw foreground
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        this.addButtons();
        this.drawActiveEntry();
    }

    private void addButtons() {
        //buttons
        int buttonX = 184;
        int buttonY = 7;
        int buttonWidth = 60;
        int buttonHeight = 12;
        this.buttonList.add(new GuiButton(buttonIdGrowth, this.guiLeft + buttonX, this.guiTop + buttonY, buttonWidth, buttonHeight, "Growth"));
        this.buttonList.add(new GuiButton(buttonIdGain, this.guiLeft + buttonX, this.guiTop + buttonY+buttonHeight+1, buttonWidth, buttonHeight, "Gain"));
        this.buttonList.add(new GuiButton(buttonIdStrength, this.guiLeft +  buttonX, this.guiTop + buttonY+2*(buttonHeight+1), buttonWidth, buttonHeight, "Strength"));
        //seed buttons
        int xOffset = 7;
        int yOffset = 8;
        ArrayList<ItemStack> seedStacks = this.getSeedStacks();
        for(int i=0;i<seedStacks.size();i++) {
            this.buttonList.add(new SeedButton(buttonIdStrength+1+i, this.guiLeft + xOffset + (16*i)%64, this.guiTop + yOffset + 16*(i/4), seedStacks.get(i)));
        }
    }

    private void drawActiveEntry() {
        if(this.activeEntry!=null && this.activeEntry.getItem()!=null) {
            ArrayList<SlotSeedStorage> activeEntries = ( this.entries ).get(this.activeEntry.getItem()).get(this.activeEntry.getItemDamage());
            for(Slot slot:activeEntries) {

            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    //opening the gui doesn't pause the game
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    //seed button class
    protected static class SeedButton extends GuiButton {
        public ResourceLocation texture;
        public String tooltip;

        public SeedButton(int id, int xPos, int yPos, ItemStack seedStack) {
            super(id, xPos, yPos, 16, 16, "");
            this.texture = RenderHelper.getItemResource(seedStack.getItem().getIconFromDamage(seedStack.getItemDamage()));
            this.tooltip = seedStack.getDisplayName();
        }

        //copied from vanilla code, just replaced the texture
        @Override
        public void drawButton(Minecraft minecraft, int cursorX, int cursorY){
            if (this.visible){
                minecraft.getTextureManager().bindTexture(this.texture);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                //checks if the button is highlighted or not
                this.field_146123_n = cursorX >= this.xPosition && cursorY >= this.yPosition && cursorX < this.xPosition + this.width && cursorY < this.yPosition + this.height;
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0, this.width, this.height);
                this.mouseDragged(minecraft, cursorX, cursorY);
            }
        }

        @Override
        public void drawTexturedModalRect(int xPos, int yPos, int u, int v, int width, int height) {
            float f = Constants.unit;
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(xPos, yPos + height, this.zLevel, (double) u*f, (double) (v+height)*f);
            tessellator.addVertexWithUV(xPos + width, yPos + height, this.zLevel, (double) (u+width)*f, (double) (v+height)*f);
            tessellator.addVertexWithUV(xPos + width, yPos, this.zLevel, (double) (u+width)*f, (double) v*f);
            tessellator.addVertexWithUV(xPos, yPos, this.zLevel, (double) u*f, (double) v*f);
            tessellator.draw();
        }
    }
}
