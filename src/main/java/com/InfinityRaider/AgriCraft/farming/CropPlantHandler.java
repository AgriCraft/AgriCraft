package com.InfinityRaider.AgriCraft.farming;

import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftPlant;
import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.*;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import com.InfinityRaider.AgriCraft.utility.exception.BlacklistedCropPlantException;
import com.InfinityRaider.AgriCraft.utility.exception.DuplicateCropPlantException;
import net.minecraft.block.BlockCrops;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashMap;

public class CropPlantHandler {
    private static HashMap<Item, HashMap<Integer, CropPlant>> cropPlants = new HashMap<Item, HashMap<Integer, CropPlant>>();
    private static ArrayList<CropPlant> plantsToRegister = new ArrayList<CropPlant>();

    public static void registerPlant(IAgriCraftPlant plant) throws DuplicateCropPlantException, BlacklistedCropPlantException {
        registerPlant(new CropPlantAgriCraft(plant));
    }

    public static void registerPlant(CropPlant plant) throws DuplicateCropPlantException, BlacklistedCropPlantException {
        ItemStack stack = plant.getSeed();
        if(SeedHelper.isSeedBlackListed(stack)) {
            throw new BlacklistedCropPlantException();
        }
        LogHelper.debug("Registering plant for "+stack.getUnlocalizedName());
        Item seed = stack.getItem();
        int meta = stack.getItemDamage();
        HashMap<Integer, CropPlant> entryForSeed = cropPlants.get(seed);
        if(entryForSeed!=null) {
            if(entryForSeed.get(meta)!=null) {
                throw new DuplicateCropPlantException();
            }
            else {
                entryForSeed.put(meta, plant);
            }
        }
        else {
            entryForSeed = new HashMap<Integer, CropPlant>();
            entryForSeed.put(meta, plant);
            cropPlants.put(seed, entryForSeed);
        }
    }

    public static void addCropToRegister(CropPlant plant) {
        plantsToRegister.add(plant);
    }

    public static boolean isValidSeed(ItemStack stack) {
        try {
            return cropPlants.get(stack.getItem()).get(stack.getItemDamage())!=null;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static NBTTagCompound writePlantToNBT(CropPlant plant) {
        NBTTagCompound tag = new NBTTagCompound();
        plant.getSeed().writeToNBT(tag);
        return tag;
    }

    public static CropPlant readPlantFromNBT(NBTTagCompound tag) {
        return getPlantFromStack(ItemStack.loadItemStackFromNBT(tag));
    }

    public static CropPlant getPlantFromStack(ItemStack stack) {
        try {
            return cropPlants.get(stack.getItem()).get(stack.getItemDamage());
        } catch(NullPointerException nullPointerException) {
            return null;
        }
    }

    public static ArrayList<CropPlant> getPlants() {
        ArrayList<CropPlant> plants = new ArrayList<CropPlant>();
        for(HashMap<Integer, CropPlant> subMap:cropPlants.values()) {
            plants.addAll(subMap.values());
        }
        return plants;
    }

    public static void init() {
        //register vanilla plants
        try {
            registerPlant(new CropPlantVanilla((BlockCrops) net.minecraft.init.Blocks.wheat, (ItemSeeds) net.minecraft.init.Items.wheat_seeds));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            OreDictionary.registerOre("seedMelon", Items.melon_seeds);
            OreDictionary.registerOre("cropMelon", Items.melon);
            registerPlant(new CropPlantStem((ItemSeeds) Items.melon_seeds, Blocks.melon_block));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            OreDictionary.registerOre("seedPumpkin", Items.pumpkin_seeds);
            OreDictionary.registerOre("cropPumpkin", Blocks.pumpkin);
            registerPlant(new CropPlantStem((ItemSeeds) Items.pumpkin_seeds, Blocks.pumpkin));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            registerPlant(new CropPlantNetherWart());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //register mod crops
        ModHelper.initModPlants();
        //register crops specified trough the API
        for (CropPlant plant : plantsToRegister) {
            try {
                registerPlant(plant);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        plantsToRegister = null;
        //register others from ore dictionary
        ArrayList<ItemStack> seeds = OreDictionary.getOres(Names.OreDict.listAllseed);
        for (ItemStack seed : seeds) {
            if (isValidSeed(seed)) {
                //seed is already registered
                continue;
            }
            if (!(seed.getItem() instanceof ItemSeeds)) {
                //seed does not extend ItemSeeds
                continue;
            }
            ArrayList<ItemStack> fruits = OreDictHelper.getFruitsFromOreDict(seed);
            if (fruits == null || fruits.size() == 0) {
                //seed and/or fruit is not properly registered
                continue;
            }
            try {
                registerPlant(new CropPlantOreDict((ItemSeeds) seed.getItem()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
