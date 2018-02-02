package com.infinityraider.agricraft.gui.storage;

import com.infinityraider.agricraft.container.ContainerSeedStorageController;
import com.infinityraider.agricraft.gui.AgriGuiWrapper;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.tiles.storage.TileEntitySeedStorageController;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSeedStorageController extends GuiSeedStorageBase {

    private static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_seed_storage_controller.png");
    private static final int sizeX = 250;
    private static final int sizeY = 176;

    public GuiSeedStorageController(InventoryPlayer inventory, TileEntitySeedStorageController te) {
        super(new ContainerSeedStorageController(te, inventory), 10, 6, 184, 7, 7, 8, 82, 8);
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
