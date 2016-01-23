package com.InfinityRaider.AgriCraft.compatibility.ganysMods;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v1.RequirementType;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class CropPlantGanysNether extends CropPlant {
    private final Item seed;
    private final Block plant;
    private final ItemStack fruit;

    /**
     * 0: like wheat
     * 1: bush
     * 2: stem
     */
    private final int renderMethod;

    protected CropPlantGanysNether(Item seed, Block plant, ItemStack fruit, int renderMethod) {
        super();
        this.seed = seed;
        this.plant = plant;
        this.fruit = fruit;
        this.renderMethod = renderMethod;
    }

    @Override
    public int tier() {
        return 3;
    }

    @Override
    public ItemStack getSeed() {
        return new ItemStack(seed);
    }

    @Override
    public Block getBlock() {
        return plant;
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> fruits = new ArrayList<ItemStack>();
        fruits.add(fruit.copy());
        return fruits;
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return fruit.copy();
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        int amount = (int) (Math.ceil((gain + 0.00) / 3));
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        while(amount>0) {
            list.add(getRandomFruit(rand));
            amount--;
        }
        return list;
    }

    @Override
    public boolean canBonemeal() {
        return true;
    }

    @Override
    protected IGrowthRequirement initGrowthRequirement() {
        return GrowthRequirementHandler.getNewBuilder()
                .soil(new BlockWithMeta((Block) Block.blockRegistry.getObject("ganysnether:tilledNetherrack")))
                .requiredBlock(new BlockWithMeta(Blocks.lava), RequirementType.NEARBY, false)
                .build();
    }

    @Override
    public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getHeight(int meta) {
        return Constants.UNIT*13;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getPlantIcon(int growthStage) {
        return getBlock().getIcon(0, growthStage);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsFlower() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        String name = seed.getUnlocalizedName();
        String[] split = name.split("\\.");
        return "agricraft_journal.ganysNether_"+split[split.length-1];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        switch(renderMethod) {
            case 0 :
                super.renderPlantInCrop(world, x, y, z, renderer);
                break;
            case 1:
                renderer.renderStandardBlock(plant, x, y, z);
                break;
            case 2:
                int meta = world.getBlockMetadata(x, y, z);
                PlantRenderer.renderStemPlant(x, y, z, renderer, getPlantIcon(meta), meta, getBlock(), Blocks.skull, meta >= Constants.MATURE);
        }
    }
}
