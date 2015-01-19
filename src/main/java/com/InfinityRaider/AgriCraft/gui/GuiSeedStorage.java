package com.InfinityRaider.AgriCraft.gui;

import com.InfinityRaider.AgriCraft.container.ContainerSeedStorage;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedStorage;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GuiSeedStorage extends GuiContainer {
    public static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/GuiSeedStorage.png");
    private ArrayList<ItemStack> seedStacks;
    private ItemStack activeEntry;
    private int scrollPositionEntries;
    private int scrollPositionActive;

    private static int buttonIdGrowth = 0;
    private static int buttonIdGain = 1;
    private static int buttonIdStrength = 2;

    public GuiSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(new ContainerSeedStorage(inventory, te));
        this.xSize = 250;
        this.ySize = 176;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.id<=buttonIdStrength) {
            this.sortByStat(button.id);
        }
        else {
            this.setActiveEntry(button.id-1-buttonIdStrength);
        }
    }

    protected void sortByStat(int id) {

    }

    protected void setActiveEntry(int i) {
        this.activeEntry = seedStacks.get(i);
        this.scrollPositionActive = 0;
        this.updateScreen();
    }

    //draw foreground
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        this.drawButtons();
        this.drawActiveEntry();
    }

    private void drawButtons() {
        //buttons
        int buttonX = 184;
        int buttonY = 7;
        int buttonWidth = 60;
        int buttonHeight = 12;
        this.buttonList.add(new GuiButton(buttonIdGrowth, this.guiLeft + buttonX, this.guiTop + buttonY, buttonWidth, buttonHeight, "Growth"));
        this.buttonList.add(new GuiButton(buttonIdGain, this.guiLeft + buttonX, this.guiTop + buttonY+buttonHeight+1, buttonWidth, buttonHeight, "Gain"));
        this.buttonList.add(new GuiButton(buttonIdStrength, this.guiLeft +  buttonX, this.guiTop + buttonY+2*(buttonHeight+1), buttonWidth, buttonHeight, "Strength"));
        //seed buttons
        this.loadSeedStacks();
        int xOffset = 7;
        int yOffset = 8;
        for(int i=0;i<seedStacks.size();i++) {
            this.buttonList.add(new SeedButton(buttonIdStrength+1+i, this.guiLeft + xOffset, this.guiTop + yOffset, seedStacks.get(i)));
            xOffset = xOffset+16;
            yOffset = yOffset+xOffset>64?16:0;
            xOffset = xOffset%64;
        }
    }

    private void drawActiveEntry() {
        if(this.activeEntry!=null && this.activeEntry.getItem()!=null) {
            ArrayList<Slot> activeEntries = ( ((ContainerSeedStorage) this.inventorySlots).entries ).get(this.activeEntry.getItem()).get(this.activeEntry.getItemDamage());
            for(Slot slot:activeEntries) {

            }
        }
    }

    private void loadSeedStacks() {
        seedStacks = new ArrayList<ItemStack>();
        HashMap<ItemSeeds, HashMap<Integer, ArrayList<Slot>>> stored = ((ContainerSeedStorage) this.inventorySlots).entries;
        for(Map.Entry<ItemSeeds, HashMap<Integer, ArrayList<Slot>>> seedItemEntry:stored.entrySet()) {
            for(Map.Entry<Integer, ArrayList<Slot>> seedMetaEntry:seedItemEntry.getValue().entrySet()) {
                seedStacks.add(new ItemStack(seedItemEntry.getKey(), seedMetaEntry.getKey()));
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
        public void drawButton(Minecraft minecraft, int x, int y){
            if (this.visible){
                FontRenderer fontrenderer = minecraft.fontRenderer;
                minecraft.getTextureManager().bindTexture(this.texture);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.field_146123_n = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
                int k = this.getHoverState(this.field_146123_n);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + k * 20, this.width / 2, this.height);
                this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
                this.mouseDragged(minecraft, x, y);
                int l = 14737632;
                if (packedFGColour != 0) {l = packedFGColour;}
                else if (!this.enabled) {l = 10526880;}
                else if (this.field_146123_n) {l = 16777120;}
                this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
            }
        }
    }
}
