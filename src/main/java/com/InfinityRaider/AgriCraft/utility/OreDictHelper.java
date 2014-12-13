package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.init.Items;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;

//lots of ctrl+c & ctrl+v (read: bad programming) in this class, viewer discretion is advised.
//not for sensitive programmer eyes
public abstract class OreDictHelper {
    public static Block oreCopper;
    public static Block oreTin;
    public static Block oreLead;
    public static Block oreSilver;
    public static Block oreAluminum;
    public static Block oreNickel;
    public static Block orePlatinum ;
    public static Block oreOsmium;

    public static int oreCopperMeta;
    public static int oreTinMeta;
    public static int oreLeadMeta;
    public static int oreSilverMeta;
    public static int oreAluminumMeta;
    public static int oreNickelMeta;
    public static int orePlatinumMeta;
    public static int oreOsmiumMeta;

    //checks if an itemstack has this ore dictionary entry
    public static boolean hasOreId(ItemStack stack, String tag) {
        int[] ids = OreDictionary.getOreIDs(stack);
        for(int id:ids) {
            if(OreDictionary.getOreName(id).equals(tag)) {
                return true;
            }
        }
        return false;
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
    public static ItemStack getIngot(String nugget) {
        ItemStack ingot = null;
        if(nugget.length()>6) {
            ArrayList<ItemStack> entries = OreDictionary.getOres("ingot." + nugget.substring(6));
            if(entries.size()>0 && entries.get(0).getItem()!=null) {
                ingot = entries.get(0);
            }
        }
        return ingot;
    }

    //finds what ores and nuggets are already registered in the ore dictionary
    public static void getRegisteredOres() {
        String[] oreDictNames = OreDictionary.getOreNames();
        for (String ore:oreDictNames) {
            if(ore.equals(Names.Ores.oreCopper)) {
                boolean flag = true;
                int i = 0;
                while(flag && i<OreDictionary.getOres(ore).size()) {
                    if(OreDictionary.getOres(ore).get(i).getItem() instanceof  ItemBlock) {
                        oreCopper = ((ItemBlock) OreDictionary.getOres(ore).get(i).getItem()).field_150939_a;
                        oreCopperMeta = OreDictionary.getOres(ore).get(i).getItemDamage();
                        flag = false;
                    }
                    i++;
                }
            }
            else if(ore.equals(Names.Ores.oreTin)) {
                boolean flag = true;
                int i = 0;
                while(flag && i<OreDictionary.getOres(ore).size()) {
                    if(OreDictionary.getOres(ore).get(i).getItem() instanceof  ItemBlock) {
                        oreTin = ((ItemBlock) OreDictionary.getOres(ore).get(i).getItem()).field_150939_a;
                        oreTinMeta = OreDictionary.getOres(ore).get(i).getItemDamage();
                        flag = false;
                    }
                    i++;
                }
            }
            else if(ore.equals(Names.Ores.oreLead)) {
                boolean flag = true;
                int i = 0;
                while(flag && i<OreDictionary.getOres(ore).size()) {
                    if(OreDictionary.getOres(ore).get(i).getItem() instanceof  ItemBlock) {
                        oreLead = ((ItemBlock) OreDictionary.getOres(ore).get(i).getItem()).field_150939_a;
                        oreLeadMeta = OreDictionary.getOres(ore).get(i).getItemDamage();
                        flag = false;
                    }
                    i++;
                }
            }
            else if(ore.equals(Names.Ores.oreSilver)) {
                boolean flag = true;
                int i = 0;
                while(flag && i<OreDictionary.getOres(ore).size()) {
                    if(OreDictionary.getOres(ore).get(i).getItem() instanceof  ItemBlock) {
                        oreSilver = ((ItemBlock) OreDictionary.getOres(ore).get(i).getItem()).field_150939_a;
                        oreSilverMeta = OreDictionary.getOres(ore).get(i).getItemDamage();
                        flag = false;
                    }
                    i++;
                }
            }
            else if(ore.equals(Names.Ores.oreAluminum)) {
                boolean flag = true;
                int i = 0;
                while(flag && i<OreDictionary.getOres(ore).size()) {
                    if(OreDictionary.getOres(ore).get(i).getItem() instanceof  ItemBlock) {
                        oreAluminum = ((ItemBlock) OreDictionary.getOres(ore).get(i).getItem()).field_150939_a;
                        oreAluminumMeta = OreDictionary.getOres(ore).get(i).getItemDamage();
                        flag = false;
                    }
                    i++;
                }
            }
            else if(ore.equals(Names.Ores.oreNickel)) {
                boolean flag = true;
                int i = 0;
                while(flag && i<OreDictionary.getOres(ore).size()) {
                    if(OreDictionary.getOres(ore).get(i).getItem() instanceof  ItemBlock) {
                        oreNickel = ((ItemBlock) OreDictionary.getOres(ore).get(i).getItem()).field_150939_a;
                        oreNickelMeta = OreDictionary.getOres(ore).get(i).getItemDamage();
                        flag = false;
                    }
                    i++;
                }
            }
            else if(ore.equals(Names.Ores.orePlatinum)) {
                boolean flag = true;
                int i = 0;
                while(flag && i<OreDictionary.getOres(ore).size()) {
                    if(OreDictionary.getOres(ore).get(i).getItem() instanceof  ItemBlock) {
                        orePlatinum= ((ItemBlock) OreDictionary.getOres(ore).get(i).getItem()).field_150939_a;
                        orePlatinumMeta = OreDictionary.getOres(ore).get(i).getItemDamage();
                        flag = false;
                    }
                    i++;
                }
            }
            else if(ore.equals(Names.Ores.oreOsmium)) {
                boolean flag = true;
                int i = 0;
                while(flag && i<OreDictionary.getOres(ore).size()) {
                    if(OreDictionary.getOres(ore).get(i).getItem() instanceof  ItemBlock) {
                        oreOsmium = ((ItemBlock) OreDictionary.getOres(ore).get(i).getItem()).field_150939_a;
                        oreOsmiumMeta = OreDictionary.getOres(ore).get(i).getItemDamage();
                        flag = false;
                    }
                    i++;
                }
            }
            else if(ore.equals(Names.Nuggets.nuggetDiamond)) {
                if(OreDictionary.getOres(ore).size()>0) {
                    Items.nuggetDiamond = OreDictionary.getOres(ore).get(0).getItem();
                    Items.nuggetDiamondMeta = OreDictionary.getOres(ore).get(0).getItemDamage();
                }
            }
            else if(ore.equals(Names.Nuggets.nuggetEmerald)) {
                if(OreDictionary.getOres(ore).size()>0) {
                    Items.nuggetEmerald = OreDictionary.getOres(ore).get(0).getItem();
                    Items.nuggetEmeraldMeta = OreDictionary.getOres(ore).get(0).getItemDamage();
                }
            }
            else if(ore.equals(Names.Nuggets.nuggetIron)) {
                if(OreDictionary.getOres(ore).size()>0) {
                    Items.nuggetIron = OreDictionary.getOres(ore).get(0).getItem();
                    Items.nuggetIronMeta = OreDictionary.getOres(ore).get(0).getItemDamage();
                }
            }
            else if(ore.equals(Names.Nuggets.nuggetCopper)) {
                if(OreDictionary.getOres(ore).size()>0) {
                    Items.nuggetCopper = OreDictionary.getOres(ore).get(0).getItem();
                    Items.nuggetCopperMeta = OreDictionary.getOres(ore).get(0).getItemDamage();
                }
            }
            else if(ore.equals(Names.Nuggets.nuggetTin)) {
                if(OreDictionary.getOres(ore).size()>0) {
                    Items.nuggetTin = OreDictionary.getOres(ore).get(0).getItem();
                    Items.nuggetTinMeta = OreDictionary.getOres(ore).get(0).getItemDamage();
                }
            }
            else if(ore.equals(Names.Nuggets.nuggetLead)) {
                if(OreDictionary.getOres(ore).size()>0) {
                    Items.nuggetLead = OreDictionary.getOres(ore).get(0).getItem();
                    Items.nuggetLeadMeta = OreDictionary.getOres(ore).get(0).getItemDamage();
                }
            }
            else if(ore.equals(Names.Nuggets.nuggetSilver)) {
                if(OreDictionary.getOres(ore).size()>0) {
                    Items.nuggetSilver = OreDictionary.getOres(ore).get(0).getItem();
                    Items.nuggetSilverMeta = OreDictionary.getOres(ore).get(0).getItemDamage();
                }
            }
            else if(ore.equals(Names.Nuggets.nuggetAluminum)) {
                if(OreDictionary.getOres(ore).size()>0) {
                    Items.nuggetAluminum = OreDictionary.getOres(ore).get(0).getItem();
                    Items.nuggetAluminumMeta = OreDictionary.getOres(ore).get(0).getItemDamage();
                }
            }
            else if(ore.equals(Names.Nuggets.nuggetNickel)) {
                if(OreDictionary.getOres(ore).size()>0) {
                    Items.nuggetNickel = OreDictionary.getOres(ore).get(0).getItem();
                    Items.nuggetNickelMeta = OreDictionary.getOres(ore).get(0).getItemDamage();
                }
            }
            else if(ore.equals(Names.Nuggets.nuggetPlatinum)) {
                if(OreDictionary.getOres(ore).size()>0) {
                    Items.nuggetPlatinum = OreDictionary.getOres(ore).get(0).getItem();
                    Items.nuggetPlatinumMeta = OreDictionary.getOres(ore).get(0).getItemDamage();
                }
            }
            else if(ore.equals(Names.Nuggets.nuggetOsmium)) {
                if(OreDictionary.getOres(ore).size()>0) {
                    Items.nuggetOsmium = OreDictionary.getOres(ore).get(0).getItem();
                    Items.nuggetOsmiumMeta = OreDictionary.getOres(ore).get(0).getItemDamage();
                }
            }
        }
    }
}
