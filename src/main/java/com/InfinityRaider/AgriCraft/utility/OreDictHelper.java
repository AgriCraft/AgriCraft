package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.items.ItemAgricraft;
import com.InfinityRaider.AgriCraft.items.ItemNugget;
import com.InfinityRaider.AgriCraft.reference.Data;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class OreDictHelper {

    private static final Map<String, Block> oreBlocks = new HashMap<String, Block>();
    private static final Map<String, Integer> oreBlockMeta = new HashMap<String, Integer>();

    private static final Map<String, Item> nuggets = new HashMap<String, Item>();
    private static final Map<String, Integer> nuggetMeta = new HashMap<String, Integer>();

    public static Block getOreBlockForName(String name) {
        return oreBlocks.get(name);
    }

    public static int getOreMetaForName(String name) {
        return oreBlockMeta.get(name);
    }

    public static Item getNuggetForName(String name) {
        return nuggets.get(name);
    }

    public static int getNuggetMetaForName(String name) {
        return nuggetMeta.get(name);
    }

    //checks if an itemstack has this ore dictionary entry
    public static boolean hasOreId(ItemStack stack, String tag) {
        if(stack==null || stack.getItem()==null) {
            return false;
        }
        int[] ids = OreDictionary.getOreIDs(stack);
        for(int id:ids) {
            if(OreDictionary.getOreName(id).equals(tag)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasOreId(Block block, String tag) {
        return block != null && hasOreId(new ItemStack(block), tag);
    }

    //checks if two blocks have the same ore dictionary entry
    public static boolean isSameOre(Block block1, int meta1, Block block2, int meta2) {
        if(block1==block2 && meta1==meta2) {
            return true;
        }
        if(block1==null || block2==null) {
            return false;
        }
        int[] ids1 = OreDictionary.getOreIDs(new ItemStack(block1, 1, meta1));
        int[] ids2 = OreDictionary.getOreIDs(new ItemStack(block2, 1, meta2));
        for (int id1:ids1) {
            for (int id2:ids2) {
                if (id1==id2) {
                    return true;
                }
            }
        }
        return false;
    }

    //finds the ingot for a nugget ore dictionary entry
    public static ItemStack getIngot(String ore) {
        ItemStack ingot = null;
        ArrayList<ItemStack> entries = OreDictionary.getOres("ingot" + ore);
        if (entries.size() > 0 && entries.get(0).getItem() != null) {
            ingot = entries.get(0);
        }
        return ingot;
    }

    //finds what ores and nuggets are already registered in the ore dictionary
    public static void getRegisteredOres() {
        //Vanilla
        for (String oreName : Data.vanillaNuggets) {
            getOreBlock(oreName);
            if(oreBlocks.get(oreName)!=null) {
                getNugget(oreName);
            }
        }
        //Modded
        for (String[] data : Data.modResources) {
            String oreName = data[0];
            getOreBlock(oreName);
            if(oreBlocks.get(oreName)!=null) {
                getNugget(oreName);
            }
        }
    }

    private static void getOreBlock(String oreName) {
        for (ItemStack itemStack : OreDictionary.getOres("ore"+oreName)) {
            if (itemStack.getItem() instanceof ItemBlock) {
                ItemBlock block = (ItemBlock) itemStack.getItem();

                oreBlocks.put(oreName, block.field_150939_a);
                oreBlockMeta.put(oreName, itemStack.getItemDamage());
                break;
            }
        }
    }

    private static void getNugget(String oreName) {
        List<ItemStack> nuggets = OreDictionary.getOres("nugget" + oreName);
        if (!nuggets.isEmpty()) {
            Item nugget = nuggets.get(0).getItem();
            OreDictHelper.nuggets.put(oreName, nugget);
            nuggetMeta.put(oreName, nuggets.get(0).getItemDamage());
        } else {
            ItemAgricraft nugget = new ItemNugget(oreName);
            OreDictionary.registerOre("nugget"+oreName, nugget);
            OreDictHelper.nuggets.put(oreName, nugget);
            nuggetMeta.put(oreName, 0);
        }
    }

    public static ArrayList<ItemStack> getFruitsFromOreDict(ItemStack seed) {
        return getFruitsFromOreDict(seed, true);
    }

    public static ArrayList<ItemStack> getFruitsFromOreDict(ItemStack seed, boolean sameMod) {
        String seedModId = IOHelper.getModId(seed);
        ArrayList<ItemStack> fruits = new ArrayList<ItemStack>();

        for(int id:OreDictionary.getOreIDs(seed)) {
            if(OreDictionary.getOreName(id).substring(0,4).equalsIgnoreCase("seed")) {
                String name = OreDictionary.getOreName(id).substring(4);
                ArrayList<ItemStack> fromOredict = OreDictionary.getOres("crop"+name);
                for(ItemStack stack:fromOredict) {
                    if(stack==null || stack.getItem()==null) {
                        continue;
                    }
                    String stackModId = IOHelper.getModId(stack);
                    if((!sameMod) || stackModId.equals(seedModId)) {
                        fruits.add(stack);
                    }
                }
            }
        }

        return fruits;
    }
}
