package com.InfinityRaider.AgriCraft.compatibility.magicalcrops;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlantGeneric;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class CropPlantMagicalCropsBeta extends CropPlantGeneric {
    private Item drop;
    private int meta;
    private boolean highTier;

    public CropPlantMagicalCropsBeta(ItemSeeds seed, boolean highTier) {
        super(seed);
        this.highTier = highTier;
        getDropMeta();
        getDrop();
    }

    private void getDropMeta() {
        try {
            Block plant = getPlant();
            Method[] methods = plant.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.getReturnType() != int.class) {
                    //this isn't the method we're looking for
                    continue;
                }
                Class[] params = method.getParameterTypes();
                if (params == null || params.length != 1) {
                    //this isn't the method we're looking for
                    continue;
                }
                if (params[0]!=int.class) {
                    //this isn't the method we're looking for
                    continue;
                }
                //this is the method we're looking for
                Object result = method.invoke(plant, 7);
                this.meta = (Integer) result;
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.meta = -1;
        }
    }

    private void getDrop() {
        Block plant = getPlant();
        try {
            Class clazz = plant.getClass();
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                String name = method.getName();
                if (name.equals("func_149865_P")) {
                    method.setAccessible(true);
                    this.drop = (Item) method.invoke(plant);
                    if (clazz.getName().equals("com.mark719.magicalcrops.crops.BlockModMagicCropIridium")) {
                        this.highTier = true;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            if (ConfigurationHandler.debug) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage;
    }

    private boolean highTier() {
        return this.highTier;
    }

    private Block getPlant() {
        return ((ItemSeeds) getSeed().getItem()).getPlant(null, 0, 0, 0);
    }

    @Override
    public int tier() {
        return highTier()?4:3;
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        list.add(new ItemStack(drop, 1, meta));
        return list;
    }

    @Override
    public boolean canBonemeal() {
        return !highTier();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsFlower() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        String name = getSeed().getUnlocalizedName();
        name = name.substring(name.indexOf("Seeds")+"Seeds".length());
        return "agricraft_journal.mc_"+name;
    }
}