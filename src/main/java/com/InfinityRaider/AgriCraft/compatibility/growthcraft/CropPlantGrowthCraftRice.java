package com.InfinityRaider.AgriCraft.compatibility.growthcraft;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class CropPlantGrowthCraftRice extends CropPlant {
    private Item rice;
    @SideOnly(Side.CLIENT)
    private Block plant;

    CropPlantGrowthCraftRice() {
        super();
        if(FMLCommonHandler.instance().getEffectiveSide()==Side.CLIENT) {
            plant = (Block) Block.blockRegistry.getObject("Growthcraft|Rice:grc.riceBlock");
        }
        this.rice = (Item) Item.itemRegistry.getObject("Growthcraft|Rice:grc.rice");
        boolean flag;
    }

    @Override
    public int tier() {
        return 2;
    }

    @Override
    public ItemStack getSeed() {
        return new ItemStack(rice);
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> fruits = new ArrayList<ItemStack>();
        fruits.add(new ItemStack(rice));
        return fruits;
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return new ItemStack(rice);
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        int amount = (int) (Math.ceil((gain + 0.00) / 3));
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        list.add(new ItemStack(rice, amount));
        return list;
    }

    @Override
    public boolean canBonemeal() {
        return true;
    }

    @Override
    public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
        return true;
    }

    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        return GrowthRequirementHandler.getGrowthRequirement(rice, 0).canGrow(world, x, y, z);
    }

    @Override
    public float getHeight(int meta) {
        return Constants.unit*13;
    }

    @Override
    public IIcon getPlantIcon(int growthStage) {
        return plant.getIcon(0, growthStage>=6?6:growthStage);
    }

    @Override
    public boolean renderAsFlower() {
        return false;
    }

    @Override
    public String getInformation() {
        return "agricraft_journal.hc_Rice";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        RenderingRegistry.instance().renderWorldBlock(renderer, world, x, y, z, plant, plant.getRenderType());
    }
}
