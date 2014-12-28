package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.compatibility.ex_nihilo.ExNihiloHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemBlockCustomWood;
import com.InfinityRaider.AgriCraft.items.ModItem;
import com.InfinityRaider.AgriCraft.items.crafting.RecipeJournal;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.NBTHelper;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;

public class Recipes {
    public static void init() {
        //crop item
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.crops, ConfigurationHandler.cropsPerCraft), "stickWood", "stickWood", "stickWood", "stickWood"));
        if(ConfigurationHandler.cropsPerCraft==3) {GameRegistry.addShapelessRecipe(new ItemStack(net.minecraft.init.Items.stick, 6 / ConfigurationHandler.cropsPerCraft), new ItemStack(Items.crops), new ItemStack(Items.crops));}
        else {GameRegistry.addShapelessRecipe(new ItemStack(net.minecraft.init.Items.stick, 4 / ConfigurationHandler.cropsPerCraft), new ItemStack(Items.crops));}
        //seed analyzer
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.seedAnalyzer,1), "sgs", " bs", "pwp", 's', "stickWood", 'g', "paneGlass", 'b', net.minecraft.init.Blocks.stone_slab, 'p', "plankWood", 'w', "slabWood"));
        //seeds
        GameRegistry.addShapelessRecipe(new ItemStack(Seeds.seedPotato), new ItemStack(net.minecraft.init.Items.potato));
        GameRegistry.addShapelessRecipe(new ItemStack(Seeds.seedCarrot), new ItemStack(net.minecraft.init.Items.carrot));
        if(ModIntegration.LoadedMods.exNihilo) {
            GameRegistry.addShapelessRecipe(new ItemStack(Seeds.seedPotato), new ItemStack(ExNihiloHelper.seedPotato));
            GameRegistry.addShapelessRecipe(new ItemStack(Seeds.seedCarrot), new ItemStack(ExNihiloHelper.seedCarrot));
            GameRegistry.addShapelessRecipe(new ItemStack(Seeds.seedSugarcane), new ItemStack(ExNihiloHelper.seedSugarCane));
        }
        //journal
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.journal,1), "csc", "sbs", "csc", 'c', Items.crops, 's', Names.OreDict.listAllseed, 'b', net.minecraft.init.Items.writable_book));
        GameRegistry.addRecipe(new RecipeJournal());
        //trowel
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.trowel, 1, 0), "  s","ii ", 's', "stickWood", 'i', "ingotIron"));
        //magnifying glass
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.magnifyingGlass, 1, 0), "sgs", " s ", " s ", 's', "stickWood", 'g', "paneGlass"));
        //irrigation systems
        if(!ConfigurationHandler.disableIrrigation) {
            //tank & channel
            registerCustomWoodRecipes();
            //change wooden bowl recipe
            RegisterHelper.removeRecipe(new ItemStack(net.minecraft.init.Items.bowl));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(net.minecraft.init.Items.bowl, 4), "w w", " w ", 'w', Names.OreDict.slabWood));
            //sprinkler
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.sprinkler, 1), " w ", " i ", "bcb", 'w', "plankWood", 'i', "ingotIron", 'b', net.minecraft.init.Blocks.iron_bars, 'c', net.minecraft.init.Items.bucket));
        }
        //fruits
        if(ConfigurationHandler.resourcePlants) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(net.minecraft.init.Items.diamond, 1), "nnn", "nnn", "nnn", 'n',"nuggetDiamond"));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetDiamond,9),"gemDiamond"));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(net.minecraft.init.Items.emerald,1), "nnn", "nnn", "nnn", 'n', "nuggetEmerald"));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetEmerald,9),"gemEmerald"));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(net.minecraft.init.Items.iron_ingot, 1), "nnn", "nnn", "nnn", 'n', "nuggetIron"));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetIron,9),"ingotIron"));
            if(Items.nuggetCopper instanceof ModItem) {
                ItemStack ingot = OreDictHelper.getIngot(Names.Nuggets.nuggetCopper);
                if(ingot!=null && ingot.getItem()!=null) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', "nuggetCopper"));
                    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetCopper, 9), Names.Ingots.ingotCopper));
                }
            }
            if(Items.nuggetTin instanceof ModItem) {
                ItemStack ingot = OreDictHelper.getIngot(Names.Nuggets.nuggetTin);
                if(ingot!=null && ingot.getItem()!=null) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', "nuggetTin"));
                    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetTin, 9), Names.Ingots.ingotTin));
                }
            }
            if(Items.nuggetLead instanceof ModItem) {
                ItemStack ingot = OreDictHelper.getIngot(Names.Nuggets.nuggetLead);
                if(ingot!=null && ingot.getItem()!=null) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', "nuggetLead"));
                    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetLead, 9), Names.Ingots.ingotLead));
                }
            }
            if(Items.nuggetSilver instanceof ModItem) {
                ItemStack ingot = OreDictHelper.getIngot(Names.Nuggets.nuggetSilver);
                if(ingot!=null && ingot.getItem()!=null) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', "nuggetSilver"));
                    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetSilver, 9), Names.Ingots.ingotSilver));
                }
            }
            if(Items.nuggetAluminum instanceof ModItem) {
                ItemStack ingot = OreDictHelper.getIngot(Names.Nuggets.nuggetAluminum);
                if(ingot!=null && ingot.getItem()!=null) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', "nuggetAluminum"));
                    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetAluminum, 9), Names.Ingots.ingotAluminum));
                }
            }
            if(Items.nuggetNickel instanceof ModItem) {
                ItemStack ingot = OreDictHelper.getIngot(Names.Nuggets.nuggetNickel);
                if(ingot!=null && ingot.getItem()!=null) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', "nuggetNickel"));
                    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetNickel, 9), Names.Ingots.ingotNickel));
                }
            }
            if(Items.nuggetPlatinum instanceof ModItem) {
                ItemStack ingot = OreDictHelper.getIngot(Names.Nuggets.nuggetPlatinum);
                if(ingot!=null && ingot.getItem()!=null) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', "nuggetPlatinum"));
                    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetPlatinum, 9), Names.Ingots.ingotPlatinum));
                }
            }
            if(Items.nuggetOsmium instanceof ModItem) {
                ItemStack ingot = OreDictHelper.getIngot(Names.Nuggets.nuggetOsmium);
                if(ingot!=null && ingot.getItem()!=null) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', "nuggetOsmium"));
                    GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.nuggetOsmium, 9), Names.Ingots.ingotOsmium));
                }
            }
        }

        LogHelper.info("Recipes Registered");
    }

    private static void registerCustomWoodRecipes() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        ((ItemBlockCustomWood) Item.getItemFromBlock(Blocks.blockWaterTank)).getSubItems(list);
        for(ItemStack stack:list) {
            if(stack.hasTagCompound() && stack.stackTagCompound.hasKey(Names.NBT.material) && stack.stackTagCompound.hasKey(Names.NBT.materialMeta)) {
                String material = stack.stackTagCompound.getString(Names.NBT.material);
                int meta = stack.stackTagCompound.getInteger(Names.NBT.materialMeta);
                ItemStack plank = new ItemStack((Block) Block.blockRegistry.getObject(material), 1, meta);
                ItemStack tank = new ItemStack(Blocks.blockWaterTank, 1);
                ItemStack channel = new ItemStack(Blocks.blockWaterChannel, 6);
                NBTTagCompound tag = NBTHelper.getMaterialTag(plank);
                tank.stackTagCompound = (NBTTagCompound) tag.copy();
                channel.stackTagCompound = (NBTTagCompound) tag.copy();
                GameRegistry.addShapedRecipe(tank, new Object[] {"w w", "w w", "www", 'w', plank});
                GameRegistry.addShapedRecipe(channel, new Object[] {"w w", " w ", 'w', plank});
            }
        }

    }
}
