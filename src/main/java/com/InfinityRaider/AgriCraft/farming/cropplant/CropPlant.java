package com.InfinityRaider.AgriCraft.farming.cropplant;

import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v2.IAdditionalCropData;
import com.InfinityRaider.AgriCraft.api.v2.ICrop;
import com.InfinityRaider.AgriCraft.api.v3.ICropPlant;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public abstract class CropPlant implements ICropPlant {
    private IGrowthRequirement growthRequirement;
    private int tier;
    private int spreadChance;
    private boolean blackListed;
    private boolean ignoreVanillaPlantingRule;

    public CropPlant() {
        this.growthRequirement = initGrowthRequirement();
        growthRequirement = growthRequirement == null ? GrowthRequirementHandler.getNewBuilder().build() : growthRequirement;
        this.setTier(tier());
        this.blackListed = false;
        this.ignoreVanillaPlantingRule = false;
    }

    @Override
    public final int getGrowthRate() {
    	int tier = getTier();
    	
    	if (tier > 0 && tier <= Constants.GROWTH_TIER.length) {
    		return Constants.GROWTH_TIER[tier];
    	} else {
    		return Constants.GROWTH_TIER[0];
    	}
    }

    @Override
    public final int getTier() {
        return tier;
    }

    @Override
    public final void setTier(int tier) {
        tier = tier >= Constants.GROWTH_TIER.length ? Constants.GROWTH_TIER.length-1 : tier;
        tier = tier <= 0 ? 1 : tier;
        this.tier = tier;
        this.spreadChance = 100/tier;
    }

    @Override
    public final int getSpreadChance() {
        return spreadChance;
    }

    @Override
    public final void setSpreadChance(int spreadChance) {
        this.spreadChance = spreadChance;
    }

    @Override
    public final boolean isBlackListed() {
        return blackListed;
    }

    @Override
    public final void setBlackListStatus(boolean status) {
        this.blackListed = status;
    }

    @Override
    public final boolean ignoresVanillaPlantingRule() {
        return ignoreVanillaPlantingRule;
    }

    @Override
    public final void setIgnoreVanillaPlantingRule(boolean value) {
        this.ignoreVanillaPlantingRule = value;
    }

    @Override
    public abstract int tier();

    @Override
    public abstract ItemStack getSeed();

    @Override
    public abstract Block getBlock();

    @Override
    public abstract ArrayList<ItemStack> getAllFruits();

    @Override
    public abstract ItemStack getRandomFruit(Random rand);

    @Override
    public abstract ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand);

    @Override
    public boolean onHarvest(World world, int x, int y, int z, EntityPlayer player) {
        return true;
    }

    @Override
    public boolean onHarvest(World world, int x, int y, int z, com.InfinityRaider.AgriCraft.api.v3.ICrop crop, EntityPlayer player) {
        return true;
    }

    @Override
    public void onSeedPlanted(World world, int x, int y, int z) {}

    @Override
    public void onSeedPlanted(World world, int x, int y, int z, com.InfinityRaider.AgriCraft.api.v3.ICrop crop) {}

    @Override
    public void onPlantRemoved(World world, int x, int y, int z) {}

    @Override
    public void onPlantRemoved(World world, int x, int y, int z, com.InfinityRaider.AgriCraft.api.v3.ICrop crop) {}

    @Override
    public abstract boolean canBonemeal();

    @Override
    public IAdditionalCropData getInitialCropData(World world, int x, int y, int z, ICrop crop) {
        return null;
    }

    @Override
    public IAdditionalCropData readCropDataFromNBT(NBTTagCompound tag) {
        return null;
    }

    @Override
    public void onValidate(World world, int x, int y, int z, ICrop crop) {}

    @Override
    public void onInvalidate(World world, int x, int y, int z, ICrop crop) {}

    @Override
    public void onChunkUnload(World world, int x, int y, int z, ICrop crop) {}

    public final void setGrowthRequirement(IGrowthRequirement growthRequirement) {
        this.growthRequirement = growthRequirement;
    }

    @Override
    public final IGrowthRequirement getGrowthRequirement() {
        return growthRequirement;
    }

    protected abstract IGrowthRequirement initGrowthRequirement();

    @Override
    public final boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
        return true;
    }

    @Override
    public abstract boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage, com.InfinityRaider.AgriCraft.api.v3.ICrop crop);

    @Override
    @SuppressWarnings("deprecation")
    public final boolean isFertile(World world, int x, int y, int z) {
        return getGrowthRequirement().canGrow(world, x, y, z);
    }

    @Override
    public boolean isMature(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) >= Constants.MATURE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public abstract float getHeight(int meta);

    @Override
    @SideOnly(Side.CLIENT)
    public abstract IIcon getPlantIcon(int growthStage);

    @Override
    @SideOnly(Side.CLIENT)
    public abstract boolean renderAsFlower();

    @Override
    @SideOnly(Side.CLIENT)
    public abstract String getInformation();


    @Override
    @SideOnly(Side.CLIENT)
    public boolean overrideRendering() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        PlantRenderer.renderPlantLayer(world, x, y, z, renderAsFlower() ? 1 : 6, getPlantIcon(world.getBlockMetadata(x, y, z)), 0);
    }
}
