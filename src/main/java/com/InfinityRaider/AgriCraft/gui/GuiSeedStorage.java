package com.InfinityRaider.AgriCraft.gui;

import com.InfinityRaider.AgriCraft.container.ContainerSeedStorage;
import com.InfinityRaider.AgriCraft.network.MessageGuiSeedStorageClearSeed;
import com.InfinityRaider.AgriCraft.network.NetworkWrapperAgriCraft;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiSeedStorage extends GuiSeedStorageBase {
    private static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/GuiSeedStorage.png");
    private static final int sizeX = 237;
    private static final int sizeY = 131;

    public GuiSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(new ContainerSeedStorage(inventory, te), 0, 14, 170, 48, -1, -1, 5, 7);
        this.xSize = sizeX;
        this.ySize = sizeY;
        if(te.hasLockedSeed()) {
            this.activeSeed = te.getLockedSeed().getItem();
            this.activeMeta = te.getLockedSeed().getItemDamage();
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
        if(this.activeSeed!=null) {
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
        if(button.id == buttonIdScrollRight+1){
            NetworkWrapperAgriCraft.wrapper.sendToServer(new MessageGuiSeedStorageClearSeed(Minecraft.getMinecraft().thePlayer));
            this.updateScreen();
        } else {
            super.actionPerformed(button);
        }
    }
}
