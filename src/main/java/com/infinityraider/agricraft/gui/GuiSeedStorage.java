package com.infinityraider.agricraft.gui;

import com.infinityraider.agricraft.container.ContainerSeedStorage;
import com.infinityraider.agricraft.network.MessageGuiSeedStorageClearSeed;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.blocks.tiles.storage.TileEntitySeedStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiSeedStorage extends GuiSeedStorageBase {

    private static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID, "textures/gui/GuiSeedStorage.png");
    private static final int sizeX = 237;
    private static final int sizeY = 131;

    public GuiSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(new ContainerSeedStorage(inventory, te), 0, 14, 170, 48, -1, -1, 5, 7);
        this.xSize = sizeX;
        this.ySize = sizeY;
        if (te.hasLockedSeed()) {
            this.activeSeed = te.getLockedSeed().get();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void loadButtons() {
        super.loadButtons();
        this.buttonList.add(new GuiButton(buttonIdScrollRight + 1, this.guiLeft + 211, this.guiTop + 105, 18, 18, "X"));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if (this.activeSeed != null) {
            this.drawActiveEntries(texture, 6, 35);
        }
        drawScrollBarHorizontal(texture);
    }

    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        this.drawTooltip(x, y);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == buttonIdScrollRight + 1) {
            new MessageGuiSeedStorageClearSeed(Minecraft.getMinecraft().thePlayer).sendToServer();
            this.updateScreen();
        } else {
            super.actionPerformed(button);
        }
    }
}
