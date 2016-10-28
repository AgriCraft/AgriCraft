package com.infinityraider.agricraft.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.blocks.tiles.storage.TileEntitySeedStorageController;
import com.infinityraider.agricraft.container.ContainerSeedStorageController;
import com.infinityraider.agricraft.reference.Reference;

@SideOnly(Side.CLIENT)
public class GuiSeedStorageController extends GuiSeedStorageBase {
    private static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID, "textures/gui/GuiSeedStorageController.png");
    private static final int sizeX = 250;
    private static final int sizeY = 176;

    public GuiSeedStorageController(InventoryPlayer inventory, TileEntitySeedStorageController te) {
        super(new ContainerSeedStorageController(inventory, te), 10, 6, 184, 7, 7, 8, 82, 8);
        this.xSize = sizeX;
        this.ySize = sizeY;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    //draw foreground
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        if(this.activeSeed!=null) {
            this.drawActiveEntries(texture, 82, 35);
        }
    }
}
