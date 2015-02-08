package com.InfinityRaider.AgriCraft.gui;

import com.InfinityRaider.AgriCraft.container.ContainerAgricraft;
import com.InfinityRaider.AgriCraft.container.ContainerSeedStorage;
import com.InfinityRaider.AgriCraft.container.ContainerSeedStorageController;
import com.InfinityRaider.AgriCraft.container.SlotSeedStorage;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiSeedStorage extends GuiContainer {
    public static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/GuiSeedStorage.png");
    private static final int sizeX = 237;
    private static final int sizeY = 131;

    protected static final int buttonIdGrowth = 0;
    protected static final int buttonIdGain = 1;
    protected static final int buttonIdStrength = 2;

    public GuiSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(new ContainerSeedStorage(inventory, te));
        this.xSize = sizeX;
        this.ySize = sizeY;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    //draw foreground
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        this.loadButtons(170, 48);
        this.drawStatBars();
    }

    private void drawStatBars() {
        int textureSize = 256;
        int xOffset = 6;
        int yOffset = 25;
        GL11.glColor4f(1F, 1F, 1F, 1F);
        for(int i=ContainerAgricraft.PLAYER_INVENTORY_SIZE;i<this.inventorySlots.inventorySlots.size();i++) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
            Slot slot = (Slot) this.inventorySlots.inventorySlots.get(i);
            if(slot!=null && slot.getStack()!=null) {
                int growth = slot.getStack().stackTagCompound.getInteger(Names.NBT.growth);
                int gain = slot.getStack().stackTagCompound.getInteger(Names.NBT.gain);
                int strength = slot.getStack().stackTagCompound.getInteger(Names.NBT.strength);
                this.drawTexturedModalRect(xOffset+i*16+1,  yOffset-growth,   0, textureSize-growth,   3, growth);
                this.drawTexturedModalRect(xOffset+i*16+6,  yOffset-gain,     0, textureSize-gain,     3, gain);
                this.drawTexturedModalRect(xOffset+i*16+11, yOffset-strength, 0, textureSize-strength, 3, strength);
            }
        }
    }

    protected void loadButtons(int x, int y) {
        this.buttonList.clear();
        int buttonWidth = 60;
        int buttonHeight = 12;
        this.buttonList.add(new GuiButton(buttonIdGrowth, this.guiLeft + x, this.guiTop + y, buttonWidth, buttonHeight, "Growth"));
        this.buttonList.add(new GuiButton(buttonIdGain, this.guiLeft + x, this.guiTop + y+buttonHeight+1, buttonWidth, buttonHeight, "Gain"));
        this.buttonList.add(new GuiButton(buttonIdStrength, this.guiLeft +  x, this.guiTop + y+2*(buttonHeight+1), buttonWidth, buttonHeight, "Strength"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        LogHelper.debug("Pressed button: " + button.id);
        if (button.id <= buttonIdStrength) {
            this.sortByStat(button.id);
        }
        else {
            super.actionPerformed(button);
        }
        this.updateScreen();
    }

    private void sortByStat(int id) {
        String stat=null;
        switch(id) {
            case buttonIdGrowth: stat = Names.NBT.growth; break;
            case buttonIdGain: stat = Names.NBT.gain; break;
            case buttonIdStrength: stat = Names.NBT.strength; break;
        }
        if(stat!=null) {

        }
    }
}
