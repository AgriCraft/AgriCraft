package com.InfinityRaider.AgriCraft.farming.cropplant;

import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;

public abstract class CropPlantTall extends CropPlant {

    public abstract int halfMeta();

    @Override
    public boolean onHarvest(World world, int x, int y, int z) {
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
        ArrayList<ItemStack> list = this.getFruitsOnHarvest(crop.getGain(), world.rand);
        world.setBlockMetadataWithNotify(x, y, z, halfMeta(), 3);
        for(ItemStack drop:list) {
            float f = 0.7F;
            double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(world, (double)x + d0, (double)y + d1, (double)z + d2, drop);
            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
        }
        return false;
    }

    @Override
    public boolean isMature(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z)>=14;
    }

    @Override
    public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public abstract IIcon getBottomIcon(int growthStage);

    @SideOnly(Side.CLIENT)
    public boolean renderTopLayer(int growthStage) {
        return growthStage>7;
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
