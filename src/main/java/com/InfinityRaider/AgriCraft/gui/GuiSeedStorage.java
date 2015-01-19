package com.InfinityRaider.AgriCraft.gui;

import com.InfinityRaider.AgriCraft.container.ContainerSeedStorage;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GuiSeedStorage extends GuiContainer {
    public static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/GuiSeedStorage.png");
    public TileEntitySeedStorage te;
    private ArrayList<ItemStack> seedStacks;
    private ItemStack activeEntry;
    private float scrollPercent;

    public GuiSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(new ContainerSeedStorage(inventory, te));
        this.te = te;
        this.xSize = 250;
        this.ySize = 176;
    }

    //draw foreground
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        this.drawButtons();
        this.drawSeedEntries();
        this.drawActiveEntry();
    }

    private void drawButtons() {
        int buttonX = 214;
        int buttonY = 64;
        int buttonWidth = 29;
        int buttonHeight = 8;
        this.buttonList.add(new GuiButton(0, buttonX, buttonY, buttonWidth, buttonHeight, "Growth"));
        this.buttonList.add(new GuiButton(0, buttonX, buttonY+buttonHeight+1, buttonWidth, buttonHeight, "Gain"));
        this.buttonList.add(new GuiButton(0, buttonX, buttonY+2*(buttonHeight+1), buttonWidth, buttonHeight, "Gain"));
    }

    private void drawSeedEntries() {
        this.loadSeedStacks();
        int xOffset = 7;
        int yOffset = 8;
        for(ItemStack seedEntry:seedStacks) {
            IIcon icon = seedEntry.getItem().getIconFromDamage(seedEntry.getItemDamage());
            //no scrollbar needed
            if(seedStacks.size()<=40) {
                this.drawTexturedModelRectFromIcon(xOffset, yOffset, icon, xOffset+16, yOffset+16);
                xOffset = xOffset+16;
                yOffset = yOffset+xOffset>64?16:0;
                xOffset = xOffset%64;
            }
        }
    }

    private void drawActiveEntry() {
        if(this.activeEntry!=null && this.activeEntry.getItem()!=null) {

        }
    }

    private void loadSeedStacks() {
        seedStacks = new ArrayList<ItemStack>();
        HashMap<ItemSeeds, HashMap<Integer, ArrayList<ItemStack>>> stored = this.te.getContents();
        for(Map.Entry<ItemSeeds, HashMap<Integer, ArrayList<ItemStack>>> seedItemEntry:stored.entrySet()) {
            for(Map.Entry<Integer, ArrayList<ItemStack>> seedMetaEntry:seedItemEntry.getValue().entrySet()) {
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
}
