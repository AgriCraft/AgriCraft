package com.InfinityRaider.AgriCraft.gui;

import com.InfinityRaider.AgriCraft.container.ContainerSeedStorage;
import com.InfinityRaider.AgriCraft.container.SlotSeedStorage;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class GuiSeedStorage extends GuiSeedStorageDummy {
    public static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/GuiSeedStorage.png");

    public GuiSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(new ContainerSeedStorage(inventory, te));
        this.xSize = 237;
        this.ySize = 131;
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
        this.loadButtons();
        this.drawActiveEntries();
    }

    private void loadButtons() {
        this.buttonList.clear();
        //buttons
        int buttonX = 184;
        int buttonY = 7;
        int buttonWidth = 60;
        int buttonHeight = 12;
        this.buttonList.add(new GuiButton(buttonIdGrowth, this.guiLeft + buttonX, this.guiTop + buttonY, buttonWidth, buttonHeight, "Growth"));
        this.buttonList.add(new GuiButton(buttonIdGain, this.guiLeft + buttonX, this.guiTop + buttonY+buttonHeight+1, buttonWidth, buttonHeight, "Gain"));
        this.buttonList.add(new GuiButton(buttonIdStrength, this.guiLeft +  buttonX, this.guiTop + buttonY+2*(buttonHeight+1), buttonWidth, buttonHeight, "Strength"));
    }

    private void drawActiveEntries() {
        int xOffset = 82;
        int yOffset = 35;
        int textureSize = 256;
        ArrayList<SlotSeedStorage> slots = this.getActiveSlots();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
        for(int i=0;i<slots.size();i++) {
            SlotSeedStorage slot = slots.get(i);
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

    @Override
    protected void actionPerformed(GuiButton button) {
        LogHelper.debug("Pressed button: " + button.id);
        super.actionPerformed(button);
        this.updateScreen();
    }
}
