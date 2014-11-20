package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RenderChannel  extends TileEntitySpecialRenderer {
    private ResourceLocation[] baseTexture = {new ResourceLocation(Reference.MOD_ID.toLowerCase()+":textures/blocks/tankWood.png"), new ResourceLocation(Reference.MOD_ID.toLowerCase()+":textures/blocks/tankIron.png")};
    private ResourceLocation waterTexture = new ResourceLocation("minecraft:textures/blocks/water_still.png");

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        TileEntityChannel channel = (TileEntityChannel) tileEntity;
        if(channel.getBlockMetadata()==0) {
            this.renderWoodChannel(channel, x, y, z);
        }
        else if(channel.getBlockMetadata()==1) {
            this.renderIronChannel(channel, x, y, z);
        }
    }

    private void renderWoodChannel(TileEntityChannel channel, double x, double y, double z) {
        this.renderBottom(channel, x, y, z);
    }

    private void renderBottom(TileEntityChannel channel, double x, double y, double z) {
        float unit = Constants.unit;
        //bind the texture
        Minecraft.getMinecraft().renderEngine.bindTexture(baseTexture[channel.getBlockMetadata()]);
    }

    private void renderIronChannel(TileEntityChannel channel, double x, double y, double z) {

    }
}
