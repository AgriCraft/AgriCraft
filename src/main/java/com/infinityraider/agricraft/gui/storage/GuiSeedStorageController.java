package com.infinityraider.agricraft.gui.storage;

import com.infinityraider.agricraft.container.ContainerSeedStorageController;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.blocks.tiles.storage.TileEntitySeedStorageController;
import com.infinityraider.agricraft.gui.AgriGuiWrapper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSeedStorageController extends GuiSeedStorageBase {

    private static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID, "textures/gui/GuiSeedStorageController.png");
    private static final int sizeX = 250;
    private static final int sizeY = 176;

    public GuiSeedStorageController(InventoryPlayer inventory, TileEntitySeedStorageController te) {
        super(new ContainerSeedStorageController(inventory, te), 10, 6, 184, 7, 7, 8, 82, 8);
    }

    @Override
    protected void onComponentGuiInit(AgriGuiWrapper wrapper) {
        this.clearBackgrounds();
        this.addBackground(texture);
    }

    /*
    //draw foreground
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        if (this.activeSeed != null) {
            this.drawActiveEntries(texture, 82, 35);
        }
    }
     */
}
