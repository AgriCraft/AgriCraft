package com.infinityraider.agricraft.gui.storage;

import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.container.ContainerSeedStorage;
import com.infinityraider.agricraft.network.MessageGuiSeedStorageClearSeed;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.blocks.tiles.storage.TileEntitySeedStorage;
import com.infinityraider.agricraft.gui.AgriGuiWrapper;
import com.infinityraider.agricraft.gui.component.BasicComponents;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSeedStorage extends GuiSeedStorageBase {

    private static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID, "textures/gui/GuiSeedStorage.png");

    private final Optional<AgriSeed> activeSeed;

    public GuiSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(new ContainerSeedStorage(inventory, te), 0, 14, 170, 48, -1, -1, 5, 7);
        this.activeSeed = te.getLockedSeed();
    }

    @Override
    protected void onComponentGuiInit(AgriGuiWrapper wrapper) {
        super.onComponentGuiInit(wrapper);
        this.addComponent(BasicComponents.getButtonComponent("X", 211, 105, 18, 18, (c, p) -> clearSeed()));
        this.addComponent(BasicComponents.getProgressBarComponent(50, 10, 10, 50, 5));
        this.clearBackgrounds();
        this.addBackground(texture);
    }

    /*
    @Override
    public void onRenderBackground(AgriGuiWrapper wrapper, float f, int relMouseX, int relMouseY) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        wrapper.drawTexturedModalRect(0, 0, 0, 0, this.getWidth(), this.getHeight());
        if (this.activeSeed != null) {
            this.drawActiveEntries(wrapper, texture, 6, 35);
        }
        this.drawScrollBarHorizontal(wrapper, texture);
    }
     */
    private final boolean clearSeed() {
        new MessageGuiSeedStorageClearSeed(Minecraft.getMinecraft().thePlayer).sendToServer();
        //this.updateScreen();
        return true;
    }
}
