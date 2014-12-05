package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.compatibility.LoadedMods;
import com.InfinityRaider.AgriCraft.compatibility.ex_nihilo.ExNihiloHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ModItem;
import com.InfinityRaider.AgriCraft.items.crafting.RecipeCustomWood;
import com.InfinityRaider.AgriCraft.items.crafting.RecipeJournal;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class Recipes {
    public static final boolean[][] tankSchematic = {{true, false, true}, {true, false, true}, {true, true, true}};
    public static final boolean[][] channelSchematic_1 = {{true, false, true}, {false, true, false}, {false, false, false}};
    public static final boolean[][] channelSchematic_2 = {{false, false, false}, {true, false, true}, {false, true, false}};

    public static void init() {
        //crop item
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.crops, 1), "stickWood", "stickWood", "stickWood", "stickWood"));
        GameRegistry.addShapelessRecipe(new ItemStack(net.minecraft.init.Items.stick,4),new ItemStack(Items.crops));
        //seed analyzer
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.seedAnalyzer,1), "sgs", " bs", "pwp", 's', "stickWood", 'g', "paneGlass", 'b', net.minecraft.init.Blocks.stone_slab, 'p', "plankWood", 'w', "slabWood"));
        //seeds
        GameRegistry.addShapelessRecipe(new ItemStack(Seeds.seedPotato), new ItemStack(net.minecraft.init.Items.potato));
        GameRegistry.addShapelessRecipe(new ItemStack(Seeds.seedCarrot), new ItemStack(net.minecraft.init.Items.carrot));
        if(LoadedMods.exNihilo) {
            GameRegistry.addShapelessRecipe(new ItemStack(Seeds.seedPotato), new ItemStack(ExNihiloHelper.seedPotato));
            GameRegistry.addShapelessRecipe(new ItemStack(Seeds.seedCarrot), new ItemStack(ExNihiloHelper.seedCarrot));
            GameRegistry.addShapelessRecipe(new ItemStack(Seeds.seedSugarcane), new ItemStack(ExNihiloHelper.seedSugarCane));
        }
        //journal
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.journal,1), "csc", "sbs", "csc", 'c', Items.crops, 's', Names.listAllseed, 'b', net.minecraft.init.Items.writable_book));
        GameRegistry.addRecipe(new RecipeJournal());
        //tank & channel
        GameRegistry.addRecipe(new RecipeCustomWood(new ItemStack(Blocks.blockWaterTank, 1, 0), tankSchematic));
        GameRegistry.addRecipe(new RecipeCustomWood(new ItemStack(Blocks.blockWaterChannel, 6, 0), channelSchematic_1));
        GameRegistry.addRecipe(new RecipeCustomWood(new ItemStack(Blocks.blockWaterChannel, 6, 0), channelSchematic_2));
        //sprinkler
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.sprinkler, 1), " w ", " i ", "bcb", 'w', "plankWood", 'i', "ingotIron", 'b', net.minecraft.init.Blocks.iron_bars, 'c', net.minecraft.init.Items.bucket));
        //fruits
        if(ConfigurationHandler.resourcePlants) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(net.minecraft.init.Items.diamond, 1), "nnn", "nnn", "nnn", 'n',"nuggetDiamond"));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetDiamond,9),"gemDiamond"));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(net.minecraft.init.Items.emerald,1), "nnn", "nnn", "nnn", 'n', "nuggetEmerald"));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetEmerald,9),"gemEmerald"));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(net.minecraft.init.Items.iron_ingot, 1), "nnn", "nnn", "nnn", 'n', "nuggetIron"));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetIron,9),"ingotIron"));
            if(Items.nuggetCopper instanceof ModItem) {
                ItemStack ingot = OreDictHelper.getIngot(Names.nuggetCopper);
                if(ingot!=null && ingot.getItem()!=null) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', "nuggetCopper"));
                    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetCopper, 9), Names.ingotCopper));
                }
            }
            if(Items.nuggetTin instanceof ModItem) {
                ItemStack ingot = OreDictHelper.getIngot(Names.nuggetTin);
                if(ingot!=null && ingot.getItem()!=null) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', "nuggetTin"));
                    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetTin, 9), Names.ingotTin));
                }
            }
            if(Items.nuggetLead instanceof ModItem) {
                ItemStack ingot = OreDictHelper.getIngot(Names.nuggetLead);
                if(ingot!=null && ingot.getItem()!=null) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', "nuggetLead"));
                    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetLead, 9), Names.ingotLead));
                }
            }
            if(Items.nuggetSilver instanceof ModItem) {
                ItemStack ingot = OreDictHelper.getIngot(Names.nuggetSilver);
                if(ingot!=null && ingot.getItem()!=null) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', "nuggetSilver"));
                    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetSilver, 9), Names.ingotSilver));
                }
            }
            if(Items.nuggetAluminum instanceof ModItem) {
                ItemStack ingot = OreDictHelper.getIngot(Names.nuggetAluminum);
                if(ingot!=null && ingot.getItem()!=null) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', "nuggetAluminum"));
                    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetAluminum, 9), Names.ingotAluminum));
                }
            }
            if(Items.nuggetNickel instanceof ModItem) {
                ItemStack ingot = OreDictHelper.getIngot(Names.nuggetNickel);
                if(ingot!=null && ingot.getItem()!=null) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', "nuggetNickel"));
                    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetNickel, 9), Names.ingotNickel));
                }
            }
            if(Items.nuggetPlatinum instanceof ModItem) {
                ItemStack ingot = OreDictHelper.getIngot(Names.nuggetPlatinum);
                if(ingot!=null && ingot.getItem()!=null) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', "nuggetPlatinum"));
                    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetPlatinum, 9), Names.ingotPlatinum));
                }
            }
            if(Items.nuggetOsmium instanceof ModItem) {
                ItemStack ingot = OreDictHelper.getIngot(Names.nuggetOsmium);
                if(ingot!=null && ingot.getItem()!=null) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', "nuggetOsmium"));
                    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetOsmium, 9), Names.ingotOsmium));
                }
            }
        }

        LogHelper.info("Recipes Registered");
    }
}
