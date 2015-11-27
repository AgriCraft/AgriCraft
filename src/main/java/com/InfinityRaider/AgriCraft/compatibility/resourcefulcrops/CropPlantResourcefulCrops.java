package com.InfinityRaider.AgriCraft.compatibility.resourcefulcrops;

import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

public class CropPlantResourcefulCrops extends CropPlant {
    private static ResourcefulCropsAPIwrapper api;

    private final int meta;
    private final int tier;
    private final ArrayList<ItemStack> fruits;

    @SideOnly(Side.CLIENT)
    private static IIcon[] overlayIcons;
    private static boolean grabIcons = true;

    protected CropPlantResourcefulCrops(int meta) {
        if(api==null) {
            api = ResourcefulCropsAPIwrapper.getInstance();
        }
        this.meta = meta;
        this.fruits = api.getAllFruits(meta);
        this.tier = api.getTier(meta);
        if(grabIcons) {
            try {
                if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                    setOverlayIcons();
                }
            } catch (Exception e) {
                LogHelper.debug("Failed to grab overlay icons");
                LogHelper.printStackTrace(e);
            }
            grabIcons = false;
        }
    }

    @SideOnly(Side.CLIENT)
    private void setOverlayIcons() throws Exception {
        Class blockClass = Class.forName("tehnut.resourceful.crops.block.BlockRCrop");
        Field overlayIconsField = blockClass.getDeclaredField("cropOverlay");
        Object overlayIconsObject = overlayIconsField.get(getBlock());
        if(overlayIconsObject instanceof IIcon[]) {
            overlayIcons = (IIcon[]) overlayIconsObject;
        }
    }

    @Override
    public int tier() {
        return tier;
    }

    @Override
    public ItemStack getSeed() {
        return new ItemStack(api.getSeed(), 1, meta);
    }

    @Override
    public Block getBlock() {
        return api.getPlantBlock();
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        return new ArrayList<ItemStack>(fruits);
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        if(fruits.size()==0) {
            return null;
        }
        return fruits.get(rand.nextInt(fruits.size())).copy();
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        if(fruits.size()>0) {
            int amount = (int) (Math.ceil((gain + 0.00) / 3));
            while (amount > 0) {
                list.add(getRandomFruit(rand));
                amount--;
            }
        }
        return list;
    }

    @Override
    public boolean canBonemeal() {
        return getTier()<4;
    }

    @Override
    public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
        return true;
    }

    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        return GrowthRequirementHandler.getGrowthRequirement(api.getSeed(), meta).canGrow(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getHeight(int meta) {
        return Constants.UNIT*13;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getPlantIcon(int growthStage) {
        growthStage = growthStage>7?7:growthStage<0?0:growthStage;
        return getBlock().getIcon(0, growthStage);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsFlower() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        return "";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        int growthStage = world.getBlockMetadata(x, y, z);
        PlantRenderer.renderPlantLayer(x, y, z, renderer, renderAsFlower() ? 1 : 6, getPlantIcon(growthStage), 0);
        if(overlayIcons != null) {
            Color color = api.getColor(meta);
            tessellator.setColorRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            PlantRenderer.renderPlantLayer(x, y, z, renderer, renderAsFlower() ? 1 : 6, overlayIcons[growthStage], 0, false);
        }
    }
}
