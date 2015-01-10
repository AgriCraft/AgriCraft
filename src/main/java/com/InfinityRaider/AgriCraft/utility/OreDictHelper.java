package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.init.Items;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

//lots of ctrl+c & ctrl+v (read: bad programming) in this class, viewer discretion is advised.
//not for sensitive programmer eyes
public abstract class OreDictHelper {

    private static final Map<String, Block> oreBlocks = new HashMap<String, Block>();
    private static final Map<String, Item> oreItems = new HashMap<String, Item>();
    private static final Map<String, Integer> oreMetaData = new HashMap<String, Integer>();

    private static final List<String> oreNames;
    private static final List<String> nuggetNames;

    static {
        oreNames = Arrays.asList(Names.Ores.oreCopper, Names.Ores.oreTin, Names.Ores.oreLead,
                Names.Ores.oreSilver, Names.Ores.oreAluminum, Names.Ores.oreNickel,
                Names.Ores.orePlatinum, Names.Ores.oreOsmium);

        nuggetNames = Arrays.asList(Names.Nuggets.nuggetDiamond, Names.Nuggets.nuggetEmerald,
                Names.Nuggets.nuggetIron, Names.Nuggets.nuggetCopper, Names.Nuggets.nuggetTin,
                Names.Nuggets.nuggetLead, Names.Nuggets.nuggetSilver, Names.Nuggets.nuggetAluminum,
                Names.Nuggets.nuggetNickel, Names.Nuggets.nuggetPlatinum, Names.Nuggets.nuggetOsmium);
    }

    public static Block getOreBlockForName(String name) {
        return oreBlocks.get(name);
    }

    public static int getOreMetaDataForName(String name) {
        return oreMetaData.get(name);
    }

    // TODO: Get rid of instance fields!! Access blocks and metadata only through the maps (via access methods)

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
        registerOres();
        registerNuggets();

        initializeOreBlockFields();
        initializeNuggetFields();
    }

    private static void registerOres() {
        for (String oreName : oreNames) {
            for (ItemStack itemStack : OreDictionary.getOres(oreName)) {
                if (itemStack.getItem() instanceof ItemBlock) {
                    ItemBlock block = (ItemBlock) itemStack.getItem();

                    oreBlocks.put(oreName, block.field_150939_a);
                    oreMetaData.put(oreName, itemStack.getItemDamage());
                    break;
                }
            }
        }
    }

    private static void registerNuggets() {
        for (String nuggetName : nuggetNames) {
            List<ItemStack> nuggets = OreDictionary.getOres(nuggetName);
            if (!nuggets.isEmpty()) {
                Item nugget = nuggets.get(0).getItem();

                oreItems.put(nuggetName, nugget);
                oreMetaData.put(nuggetName, nuggets.get(0).getItemDamage());
            }
        }
    }

    private static void initializeOreBlockFields() {
        oreCopper = oreBlocks.get(Names.Ores.oreCopper);
        if (oreCopper != null) oreCopperMeta = oreMetaData.get(Names.Ores.oreCopper);

        oreTin = oreBlocks.get(Names.Ores.oreTin);
        if (oreTin != null) oreTinMeta = oreMetaData.get(Names.Ores.oreTin);

        oreLead = oreBlocks.get(Names.Ores.oreLead);
        if (oreLead != null) oreLeadMeta = oreMetaData.get(Names.Ores.oreLead);

        oreSilver = oreBlocks.get(Names.Ores.oreSilver);
        if (oreSilver != null) oreSilverMeta = oreMetaData.get(Names.Ores.oreSilver);

        oreAluminum = oreBlocks.get(Names.Ores.oreAluminum);
        if (oreAluminum != null) oreAluminumMeta = oreMetaData.get(Names.Ores.oreAluminum);

        oreNickel = oreBlocks.get(Names.Ores.oreNickel);
        if (oreNickel != null) oreNickelMeta = oreMetaData.get(Names.Ores.oreNickel);

        orePlatinum = oreBlocks.get(Names.Ores.orePlatinum);
        if (orePlatinum != null) orePlatinumMeta = oreMetaData.get(Names.Ores.orePlatinum);

        oreOsmium = oreBlocks.get(Names.Ores.oreOsmium);
        if (oreOsmium != null) oreOsmiumMeta = oreMetaData.get(Names.Ores.oreOsmium);
    }

    private static void initializeNuggetFields() {
        Items.nuggetDiamond = oreItems.get(Names.Nuggets.nuggetDiamond);
        if (Items.nuggetDiamond != null) Items.nuggetDiamondMeta = oreMetaData.get(Names.Nuggets.nuggetDiamond);

        Items.nuggetEmerald = oreItems.get(Names.Nuggets.nuggetEmerald);
        if (Items.nuggetEmerald != null) Items.nuggetEmeraldMeta = oreMetaData.get(Names.Nuggets.nuggetEmerald);

        Items.nuggetIron = oreItems.get(Names.Nuggets.nuggetIron);
        if (Items.nuggetIron != null) Items.nuggetIronMeta = oreMetaData.get(Names.Nuggets.nuggetIron);

        Items.nuggetCopper = oreItems.get(Names.Nuggets.nuggetCopper);
        if (Items.nuggetCopper != null) Items.nuggetCopperMeta = oreMetaData.get(Names.Nuggets.nuggetCopper);

        Items.nuggetTin = oreItems.get(Names.Nuggets.nuggetTin);
        if (Items.nuggetTin != null) Items.nuggetTinMeta = oreMetaData.get(Names.Nuggets.nuggetTin);

        Items.nuggetLead = oreItems.get(Names.Nuggets.nuggetLead);
        if (Items.nuggetLead != null) Items.nuggetLeadMeta = oreMetaData.get(Names.Nuggets.nuggetLead);

        Items.nuggetSilver = oreItems.get(Names.Nuggets.nuggetSilver);
        if (Items.nuggetSilver != null) Items.nuggetSilverMeta = oreMetaData.get(Names.Nuggets.nuggetSilver);

        Items.nuggetAluminum = oreItems.get(Names.Nuggets.nuggetAluminum);
        if (Items.nuggetAluminum != null) Items.nuggetAluminumMeta = oreMetaData.get(Names.Nuggets.nuggetAluminum);

        Items.nuggetNickel = oreItems.get(Names.Nuggets.nuggetNickel);
        if (Items.nuggetNickel != null) Items.nuggetNickelMeta = oreMetaData.get(Names.Nuggets.nuggetNickel);

        Items.nuggetEmerald = oreItems.get(Names.Nuggets.nuggetEmerald);
        if (Items.nuggetPlatinum != null) Items.nuggetEmeraldMeta = oreMetaData.get(Names.Nuggets.nuggetEmerald);
        
        Items.nuggetOsmium = oreItems.get(Names.Nuggets.nuggetOsmium);
        if (Items.nuggetOsmium != null) Items.nuggetOsmiumMeta = oreMetaData.get(Names.Nuggets.nuggetOsmium);
    }
}
