package com.InfinityRaider.AgriCraft.gui;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.container.ContainerSeedAnalyzer;
import com.InfinityRaider.AgriCraft.handler.GuiHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import com.InfinityRaider.AgriCraft.utility.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiSeedAnalyzer extends GuiContainer {
    public static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/GuiSeedAnalyzer.png");
    public TileEntitySeedAnalyzer seedAnalyzer;

    public GuiSeedAnalyzer(InventoryPlayer inventory, TileEntitySeedAnalyzer seedAnalyzer) {
        super(new ContainerSeedAnalyzer(inventory, seedAnalyzer));
        this.seedAnalyzer = seedAnalyzer;
        this.xSize = 176;
        this.ySize = 176;
    }

    //draw foreground
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        String name = "Seed Analyzer";
        int white = 4210752;        //the number for white
        //write name: x coordinate is in the middle, 6 down from the top, and setting color to white
        this.fontRendererObj.drawString(name, this.xSize/2 - this.fontRendererObj.getStringWidth(name)/2, 6, white);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, white);
        this.buttonList.add(new GuiButton(0, this.guiLeft + 131, this.guiTop + 67, 18, 18, ""));
    }

    //draw background
    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if(this.seedAnalyzer.progress > 0) {
            int state = this.seedAnalyzer.getProgressScaled(40);
            drawTexturedModalRect(this.guiLeft + 68, this.guiTop + 79, this.xSize, 0, state, 5);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        ItemStack journal = seedAnalyzer.journal;
        if(journal != null) {
            if (journal.hasTagCompound()) {
                NBTTagCompound tag = journal.stackTagCompound;
                if (tag.hasKey(Names.NBT.discoveredSeeds)) {
                    NBTTagList list = tag.getTagList(Names.NBT.discoveredSeeds, 10);
                    NBTHelper.clearEmptyStacksFromNBT(list);
                    tag.setTag(Names.NBT.discoveredSeeds, list);
                }
            }
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            player.openGui(AgriCraft.instance, GuiHandler.journalID, player.worldObj, player.serverPosX, player.serverPosY, player.serverPosZ);
        }
    }

    //opening the gui doesn't pause the game
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
