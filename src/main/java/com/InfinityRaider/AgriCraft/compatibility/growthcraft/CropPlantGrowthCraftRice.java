package com.InfinityRaider.AgriCraft.compatibility.growthcraft;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
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
    public Block getBlock() {
        return plant;
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
    protected IGrowthRequirement initGrowthRequirement() {
        return GrowthRequirementHandler.getNewBuilder().soil(new BlockWithMeta(Blocks.blockWaterPadFull)).build();
    }

    @Override
    public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
        return true;
    }

    @Override
    public float getHeight(int meta) {
        return Constants.UNIT*13;
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
    @SuppressWarnings("deprecation")
    public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        RenderingRegistry.instance().renderWorldBlock(renderer, world, x, y, z, plant, plant.getRenderType());
    }
}
