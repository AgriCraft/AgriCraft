package com.InfinityRaider.AgriCraft.compatibility.magicalcrops;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlantGeneric;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import com.mark719.magicalcrops.crops.BlockMagicalCrops;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class CropPlantMagicalCrops extends CropPlantGeneric {
    private int meta;

    public CropPlantMagicalCrops(ItemSeeds seed) {
        super(seed);
        getDropMeta();
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
                LogHelper.debug("Found method: "+method.toString());
                Object result = method.invoke(plant, 7);
                this.meta = (Integer) result;
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.meta = -1;
        }
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage;
    }

    private boolean highTier() {
        return getPlant() instanceof BlockMagicalCrops;
    }

    private Block getPlant() {
        return ((ItemSeeds) getSeed().getItem()).getPlant(null, 0, 0, 0);
    }

    @Override
    public int tier() {
        return highTier()?3:4;
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        Block plant = getPlant();
        if(highTier()) {
            BlockMagicalCrops magicPlant = (BlockMagicalCrops) plant;
            list.add(new ItemStack(magicPlant.func_149650_a(7, null, 0), 1, meta));
        } else {
            list.addAll(OreDictHelper.getFruitsFromOreDict(getSeed()));
        }
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
