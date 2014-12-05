package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.compatibility.LoadedMods;
import com.InfinityRaider.AgriCraft.compatibility.plantmegapack.PlantMegaPackHelper;
import com.InfinityRaider.AgriCraft.init.Crops;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.SeedInformation;
import mods.natura.common.NContent;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCrops;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public abstract class SeedHelper {
    public static int getSeedTier(ItemSeeds seed) {
        if(seed == null) {
            return 0;
        }
        String domain = Item.itemRegistry.getNameForObject(seed).substring(0, Item.itemRegistry.getNameForObject(seed).indexOf(':'));
        if(domain.equalsIgnoreCase("agricraft")) {
            return ((ItemModSeed) seed).getPlant().tier;
        }
        if(domain.equalsIgnoreCase("harvestcraft")) {
            return 2;
        }
        if(domain.equalsIgnoreCase("natura")) {
            return 2;
        }
        return 1;
    }

    public static int getBaseGrowth(int tier) {
        switch(tier) {
            case 1: return Constants.growthTier1;
            case 2: return Constants.growthTier2;
            case 3: return Constants.growthTier3;
            case 4: return Constants.growthTier4;
            case 5: return Constants.growthTier5;
            default: return 0;
        }
    }

    //find the crop for a seed
    public static BlockBush getPlant(ItemSeeds seed) {
        if(seed == null) {
            return null;
        }
        else if(seed == Items.melon_seeds) {
            return Crops.melon;
        }
        else if(seed == Items.pumpkin_seeds) {
            return Crops.pumpkin;
        }
        else {
            if(seed.getPlant(null, 0, 0, 0) instanceof BlockCrops) {
                return (BlockCrops) seed.getPlant(null, 0, 0, 0);
            }
            else {
                return (BlockBush) seed.getPlant(null, 0, 0, 0);
            }
        }
    }

    //gets the seed domain
    public static String getPlantDomain(ItemSeeds seed) {
        String name = Item.itemRegistry.getNameForObject(seed);
        return name.substring(0, name.indexOf(":")).toLowerCase();
    }

    //gets the fruits
    public static ArrayList<ItemStack> getPlantFruits(ItemSeeds seed, World world, int x, int y, int z, int gain, int meta) {
        int nr =  (int) (Math.ceil((gain + 0.00) / 3));
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        if(getPlant(seed)== Blocks.nether_wart) {
            items.add(new ItemStack(seed, 1, 0));
        }
        else if(LoadedMods.natura && getPlantDomain(seed).equalsIgnoreCase("natura")) {
            items.add(new ItemStack(NContent.plantItem, nr, meta*3));
        }
        else if(LoadedMods.harvestcraft && getPlantDomain(seed).equalsIgnoreCase("harvestcraft")) {
            items.add(new ItemStack(getPlant(seed).getItemDropped(7, new Random(), 0), nr));
        }
        else {
            int harvestMeta = 7;
            if(LoadedMods.plantMegaPack && getPlantDomain(seed).equalsIgnoreCase("plantmegapack")) {
                harvestMeta=PlantMegaPackHelper.getTextureIndex(seed, 7);
            }
            ArrayList<ItemStack> defaultDrops = getPlant(seed).getDrops(world, x, y, z, harvestMeta, 0);
            for (ItemStack drop : defaultDrops) {
                if (!(drop.getItem() instanceof ItemSeeds) && drop.getItem()!=null) {
                    items.add(new ItemStack(drop.getItem(), nr, drop.getItemDamage()));
                }
            }
        }
        return items;
    }

    //check if the seed is valid
    public static boolean isValidSeed(ItemSeeds seed) {
        if(LoadedMods.thaumicTinkerer && getPlantDomain(seed).equalsIgnoreCase(Names.thaumicTinkerer)) {
            LogHelper.debug("Thaumic Tinkerer infused seeds are not supported, sorry");
            return false;
        }


        return true;
    }

    //get the base growth
    public static int getBaseGrowth(ItemSeeds seed) {
        return getBaseGrowth(getSeedTier(seed));
    }

    //define NBT tag
    public static void setNBT(NBTTagCompound tag, short growth, short gain, short strength, boolean analyzed) {
        tag.setShort(Names.growth, growth==0?Constants.defaultGrowth:growth>10?10:growth);
        tag.setShort(Names.gain, gain==0?Constants.defaultGain:gain>10?10:gain);
        tag.setShort(Names.strength, strength==0?Constants.defaultGain:strength>10?10:strength);
        tag.setBoolean(Names.analyzed, analyzed);
    }

    //get a string of information about the seed for the journal
    public static String getSeedInformation(ItemStack seedStack) {
        if (!(seedStack.getItem() instanceof ItemSeeds)) {
            return null;
        }
        return SeedInformation.getSeedInformation(seedStack);
    }
}
