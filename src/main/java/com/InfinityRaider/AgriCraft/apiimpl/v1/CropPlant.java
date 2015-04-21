package com.InfinityRaider.AgriCraft.apiimpl.v1;

import com.InfinityRaider.AgriCraft.api.v1.ICropPlant;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

/** main class used by TileEntityCrop to perform its functionality, only make one object of this per seed object and register it using CropPlantHandler.registerPlant(CropPlant plant) */
public abstract class CropPlant implements ICropPlant {
    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#getGrowthRate()
		 */
    @Override
		public final int getGrowthRate() {
        switch(getTier()) {
            case 1: return Constants.growthTier1;
            case 2: return Constants.growthTier2;
            case 3: return Constants.growthTier3;
            case 4: return Constants.growthTier4;
            case 5: return Constants.growthTier5;
            default: return 0;
        }
    }

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#getTier()
		 */
    @Override
		public final int getTier() {
        int seedTierOverride = SeedHelper.getSeedTierOverride(getSeed());
        if(seedTierOverride>0) {
            return seedTierOverride;
        }
        return tier();
    }

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#tier()
		 */
    @Override
		public abstract int tier();

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#getSeed()
		 */
    @Override
		public abstract ItemStack getSeed();

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#getAllFruits()
		 */
    @Override
		public abstract ArrayList<ItemStack> getAllFruits();

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#getRandomFruit(java.util.Random)
		 */
    @Override
		public abstract ItemStack getRandomFruit(Random rand);

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#getFruitsOnHarvest(int, java.util.Random)
		 */
    @Override
		public abstract ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand);

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#onHarvest(net.minecraft.world.World, int, int, int)
		 */
    @Override
		public boolean onHarvest(World world, int x, int y, int z) {
        return true;
    }

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#onSeedPlanted(net.minecraft.world.World, int, int, int)
		 */
    @Override
		public void onSeedPlanted(World world, int x, int y, int z) {}

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#onPlantRemoved(net.minecraft.world.World, int, int, int)
		 */
    @Override
		public void onPlantRemoved(World world, int x, int y, int z) {}

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#canBonemeal()
		 */
    @Override
		public abstract boolean canBonemeal();

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#onAllowedGrowthTick(net.minecraft.world.World, int, int, int, int)
		 */
    @Override
		public abstract boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage);

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#isFertile(net.minecraft.world.World, int, int, int)
		 */
    @Override
		public abstract boolean isFertile(World world, int x, int y, int z);

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#isMature(net.minecraft.world.IBlockAccess, int, int, int)
		 */
    @Override
		public boolean isMature(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z)>=7;
    }

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#getHeight(int)
		 */
    @Override
		@SideOnly(Side.CLIENT)
    public abstract float getHeight(int meta);

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#getPlantIcon(int)
		 */
    @Override
		@SideOnly(Side.CLIENT)
    public abstract IIcon getPlantIcon(int growthStage);

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#renderAsFlower()
		 */
    @Override
		@SideOnly(Side.CLIENT)
    public abstract boolean renderAsFlower();

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#getInformation()
		 */
    @Override
		@SideOnly(Side.CLIENT)
    public abstract String getInformation();

    /* (non-Javadoc)
		 * @see com.InfinityRaider.AgriCraft.api.v1.ICropPlant#renderPlantInCrop(net.minecraft.world.IBlockAccess, int, int, int, net.minecraft.client.renderer.RenderBlocks)
		 */
    @Override
		@SideOnly(Side.CLIENT)
    public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        PlantRenderer.renderPlantLayer(x, y, z, renderer, renderAsFlower() ? 1 : 6, getPlantIcon(world.getBlockMetadata(x, y, z)), 0);
    }
}
