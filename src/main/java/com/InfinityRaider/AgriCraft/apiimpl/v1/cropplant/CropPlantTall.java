package com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant;

import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Basic abstract implementation for crops that are 2 blocks tall
 */
public abstract class CropPlantTall extends CropPlant {
    /** The metadata value for when the bottom block is "fully grown" and the second block starts growing*/
    public abstract int maxMetaBottomBlock();

    @Override
    public boolean isMature(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z)>=7;
    }

    @Override
    public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public abstract IIcon getBottomIcon(int growthStage);

    @SideOnly(Side.CLIENT)
    public boolean renderTopLayer(int growthStage) {
        return growthStage> maxMetaBottomBlock();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        int meta = world.getBlockMetadata(x, y, z);
        PlantRenderer.renderPlantLayer(x, y, z, renderer, renderAsFlower() ? 1 : 6, getBottomIcon(meta), 0);
        if(renderTopLayer(meta)) {
            PlantRenderer.renderPlantLayer(x, y, z, renderer, renderAsFlower() ? 1 : 6, getPlantIcon(meta), 1);
        }
    }
}
