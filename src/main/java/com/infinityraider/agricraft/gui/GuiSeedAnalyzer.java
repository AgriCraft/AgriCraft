package com.infinityraider.agricraft.gui;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.container.ContainerSeedAnalyzer;
import com.infinityraider.agricraft.gui.journal.GuiJournal;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.blocks.tiles.analyzer.TileEntitySeedAnalyzer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiSeedAnalyzer extends GuiContainer {
    public static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID, "textures/gui/GuiSeedAnalyzer.png");
    public TileEntitySeedAnalyzer seedAnalyzer;

    private boolean journalOpen;
    private AgriGuiWrapper guiJournal;

    public GuiSeedAnalyzer(InventoryPlayer inventory, TileEntitySeedAnalyzer seedAnalyzer) {
        super(new ContainerSeedAnalyzer(inventory, seedAnalyzer));
        this.seedAnalyzer = seedAnalyzer;
        this.xSize = 176;
        this.ySize = 176;
        this.journalOpen = false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(0, this.guiLeft + 131, this.guiTop + 67, 18, 18, ""));
    }

    //draw foreground
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        String name = AgriCore.getTranslator().translate("agricraft_gui.seedAnalyzer");
        int white = 4210752;        //the number for white
        //write name: X1 coordinate is in the middle, 6 down from the top, and setting color to white
        this.fontRendererObj.drawString(name, this.xSize/2 - this.fontRendererObj.getStringWidth(name)/2, 6, white);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 9, this.ySize - 96 + 2, white);
    }

    //draw background
    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if(this.seedAnalyzer.getProgress() > 0) {
            int state = this.seedAnalyzer.getProgressScaled(40);
            drawTexturedModalRect(this.guiLeft + 68, this.guiTop + 79, this.xSize, 0, state, 5);
        }
    }

    @Override
    public void drawScreen(int x, int y, float opacity) {
        if(journalOpen) {
            guiJournal.initGui();
            guiJournal.drawScreen(x, y, 0);
        } else {
            super.drawScreen(x, y, opacity);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(journalOpen) {
            return;
        }
        ItemStack journal = seedAnalyzer.getStackInSlot(ContainerSeedAnalyzer.journalSlotId);
        if(journal != null) {
            journalOpen = true;
            guiJournal = new AgriGuiWrapper(new GuiJournal(journal));
            guiJournal.setWorldAndResolution(this.mc, this.width, this.height);
        }
    }

    @Override
    protected void keyTyped(char key, int number) throws IOException {
        if(this.journalOpen) {
            if (number == 1 || number == this.mc.gameSettings.keyBindInventory.getKeyCode())  {
                this.journalOpen = false;
                this.guiJournal = null;
            }
            else {
                super.keyTyped(key, number);
            }
        } else {
            super.keyTyped(key, number);
        }
    }

    //opening the gui doesn't pause the game
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void mouseClicked(int x, int y, int rightClick) throws IOException {
        if(journalOpen) {
            guiJournal.mouseClicked(x, y, rightClick);
        }
        else {
            super.mouseClicked(x, y, rightClick);
        }
    }
}
