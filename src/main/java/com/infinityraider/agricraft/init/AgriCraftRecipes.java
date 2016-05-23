package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.compatibility.CompatibilityHandler;
import com.infinityraider.agricraft.handler.config.AgriCraftConfig;
import com.infinityraider.agricraft.items.ItemBase;
import com.infinityraider.agricraft.items.blocks.ItemBlockCustomWood;
import com.infinityraider.agricraft.items.crafting.RecipeJournal;
import com.infinityraider.agricraft.items.crafting.RecipeShapelessCustomWood;
import com.infinityraider.agricraft.reference.Data;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.reference.AgriCraftMods;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.utility.OreDictHelper;
import com.infinityraider.agricraft.utility.RegisterHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgriCraftRecipes {

    /** Will be replaced with all the custom woods in CustomWood recipes */
    public static final ItemStack REFERENCE = new ItemStack(net.minecraft.init.Blocks.planks, 1);

    /** Holds all the custom woods for CustomWood items, will get filled on init() */
    private static List<ItemStack> woodList;

    public static void init() {
        //crop item
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriCraftItems.crops, AgriCraftConfig.cropsPerCraft), "ss", "ss", 's', "stickWood"));
        if (AgriCraftConfig.cropsPerCraft == 3) {
            GameRegistry.addShapelessRecipe(new ItemStack(net.minecraft.init.Items.stick, 6 / AgriCraftConfig.cropsPerCraft), new ItemStack(AgriCraftItems.crops), new ItemStack(AgriCraftItems.crops));
        } else {
            GameRegistry.addShapelessRecipe(new ItemStack(net.minecraft.init.Items.stick, 4 / AgriCraftConfig.cropsPerCraft), new ItemStack(AgriCraftItems.crops));
        }
        //seed analyzer
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriCraftBlocks.blockSeedAnalyzer, 1), "sgs", " bs", "pwp", 's', "stickWood", 'g', "paneGlass", 'b', net.minecraft.init.Blocks.stone_slab, 'p', "plankWood", 'w', "slabWood"));
        //seeds
        GameRegistry.addShapelessRecipe(new ItemStack(Item.getByNameOrId("AgriCraft:seedPotato")), new ItemStack(net.minecraft.init.Items.potato));
        GameRegistry.addShapelessRecipe(new ItemStack(Item.getByNameOrId("AgriCraft:seedCarrot")), new ItemStack(net.minecraft.init.Items.carrot));
        GameRegistry.addShapelessRecipe(new ItemStack(net.minecraft.init.Items.wheat_seeds), new ItemStack(net.minecraft.init.Items.wheat));
        //journal
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriCraftItems.journal, 1), "csc", "sbs", "csc", 'c', AgriCraftItems.crops, 's', "listAllseed", 'b', net.minecraft.init.Items.writable_book));
        GameRegistry.addRecipe(new RecipeJournal());
        //trowel
        if(AgriCraftItems.enableTrowel && AgriCraftItems.trowel != null) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriCraftItems.trowel, 1, 0), "  s", "ii ", 's', "stickWood", 'i', "ingotIron"));
        }
        //magnifying glass
        if(AgriCraftItems.enableMagnifyingGlass && AgriCraftItems.magnifyingGlass != null) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriCraftItems.magnifyingGlass, 1, 0), "sgs", " s ", " s ", 's', "stickWood", 'g', "paneGlass"));
        }
        //hand rakes
        if(AgriCraftItems.enableHandRake && AgriCraftItems.handRake != null) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriCraftItems.handRake, 1, 0), "fs", 'f', net.minecraft.init.Blocks.oak_fence, 's', "stickWood"));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriCraftItems.handRake, 1, 0), "fs", 'f', net.minecraft.init.Blocks.birch_fence, 's', "stickWood"));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriCraftItems.handRake, 1, 0), "fs", 'f', net.minecraft.init.Blocks.spruce_fence, 's', "stickWood"));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriCraftItems.handRake, 1, 0), "fs", 'f', net.minecraft.init.Blocks.acacia_fence, 's', "stickWood"));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriCraftItems.handRake, 1, 0), "fs", 'f', net.minecraft.init.Blocks.jungle_fence, 's', "stickWood"));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriCraftItems.handRake, 1, 0), "fs", 'f', net.minecraft.init.Blocks.dark_oak_fence, 's', "stickWood"));
            if(AgriCraftBlocks.enableFences && AgriCraftBlocks.blockFence != null) {
                GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriCraftItems.handRake, 1, 0), "fs", 'f', AgriCraftBlocks.blockFence, 's', "stickWood"));
            }
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriCraftItems.handRake, 1, 1), "fs", 'f', net.minecraft.init.Blocks.iron_bars, 's', "stickWood"));
        }
        //clipper
        if(AgriCraftItems.enableClipper && AgriCraftItems.clipper != null) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriCraftItems.clipper, 1, 0), " i ", "scn", " s ", 'i', "ingotIron", 's', "stickWood", 'c', new ItemStack(net.minecraft.init.Items.shears)));
        }
        //peripheral
        if(AgriCraftBlocks.blockPeripheral!=null) {
            if(CompatibilityHandler.getInstance().isCompatibilityEnabled(AgriCraftMods.computerCraft)) {
                //GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriCraftBlocks.blockPeripheral, 1), "iai", "rcr", "iri", 'i', "ingotIron", 'a', AgriCraftBlocks.blockSeedAnalyzer, 'r', net.minecraft.init.AgriCraftItems.comparator, 'c', ComputerCraftHelper.getComputerBlock()));
            }
            if(CompatibilityHandler.getInstance().isCompatibilityEnabled(AgriCraftMods.openComputers)) {
                //GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriCraftBlocks.blockPeripheral, 1), "iai", "rcr", "iri", 'i', "ingotIron", 'a', AgriCraftBlocks.blockSeedAnalyzer, 'r', net.minecraft.init.AgriCraftItems.comparator, 'c', OpenComputersHelper.getComputerBlock()));
            }
        }
        //CustomWood recipes
        registerCustomWoodRecipes();
        if (!AgriCraftConfig.disableIrrigation) {
            //change wooden bowl recipe
            RegisterHelper.removeRecipe(new ItemStack(net.minecraft.init.Items.bowl));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(net.minecraft.init.Items.bowl, 4), "w w", " w ", 'w', "slabWood"));
            //sprinkler
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriCraftBlocks.blockSprinkler, 1), " w ", " i ", "bcb", 'w', "plankWood", 'i', "ingotIron", 'b', net.minecraft.init.Blocks.iron_bars, 'c', net.minecraft.init.Items.bucket));
        }
        //fruits
        if (AgriCraftConfig.resourcePlants) {
            if (OreDictHelper.getNuggetForName("Diamond") instanceof ItemBase) {
                GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(net.minecraft.init.Items.diamond, 1), "nnn", "nnn", "nnn", 'n', "nuggetDiamond"));
                GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(OreDictHelper.getNuggetForName("Diamond"), 9), "gemDiamond"));
            }
            if (OreDictHelper.getNuggetForName("Emerald") instanceof ItemBase) {
                GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(net.minecraft.init.Items.emerald, 1), "nnn", "nnn", "nnn", 'n', "nuggetEmerald"));
                GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(OreDictHelper.getNuggetForName("Emerald"), 9), "gemEmerald"));
            }
            if (OreDictHelper.getNuggetForName("Iron") instanceof ItemBase) {
                GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(net.minecraft.init.Items.iron_ingot, 1), "nnn", "nnn", "nnn", 'n', "nuggetIron"));
                GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(OreDictHelper.getNuggetForName("Iron"), 9), "ingotIron"));
            }
            if (OreDictHelper.getNuggetForName("Quartz") instanceof ItemBase) {
                GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(net.minecraft.init.Items.quartz, 1), "nnn", "nnn", "nnn", 'n', "nuggetQuartz"));
                GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(OreDictHelper.getNuggetForName("Quartz"), 9), new ItemStack(net.minecraft.init.Items.quartz, 1)));
            }
            for (String[] data : Data.modResources) {
                String oreName = data[0];
                Item nuggetItem = OreDictHelper.getNuggetForName(oreName);
                if (nuggetItem != null && nuggetItem instanceof ItemBase) {
                    ItemStack nugget = new ItemStack(nuggetItem, 9, OreDictHelper.getNuggetMetaForName(oreName));
                    ItemStack ingot = OreDictHelper.getIngot(oreName);
                    if (ingot != null) {
                        GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', "nugget" + oreName));
                    }
                    GameRegistry.addRecipe(new ShapelessOreRecipe(nugget, "ingot" + oreName));
                }
            }
            AgriCore.getLogger("AgriCraft").debug("Recipes Registered");
        }
    }

    private static void registerCustomWoodRecipes() {
        if(initWoodList()) {
            if (!AgriCraftConfig.disableIrrigation) {
                ItemStack channel = new ItemStack(AgriCraftBlocks.blockWaterChannel, 1);
                ItemStack channelFull = new ItemStack(AgriCraftBlocks.blockWaterChannelFull, 1);

                registerCustomWoodRecipe(AgriCraftBlocks.blockWaterTank, 1, true, "w w", "w w", "www", 'w', REFERENCE);
                registerCustomWoodRecipe(AgriCraftBlocks.blockWaterChannel, 6, true, "w w", " w ", 'w', REFERENCE);
                registerCustomWoodRecipe(AgriCraftBlocks.blockWaterChannelFull, 1, false, channel, channel, channel, channel);
                registerCustomWoodRecipe(AgriCraftBlocks.blockWaterChannel, 4, false, channelFull);
                registerCustomWoodRecipe(AgriCraftBlocks.blockChannelValve, 1, false, new ItemStack(net.minecraft.init.Items.iron_ingot, 1), new ItemStack(net.minecraft.init.Blocks.lever, 1), channel);
            }
            if (!AgriCraftConfig.disableSeedStorage) {
                registerCustomWoodRecipe(AgriCraftBlocks.blockSeedStorage, 1, true, "wiw", "wcw", "wcw", 'w', REFERENCE, 'i', net.minecraft.init.Items.iron_ingot, 'c', net.minecraft.init.Blocks.chest);
            }
            if (AgriCraftBlocks.enableFences) {
                ItemStack fence = new ItemStack(AgriCraftBlocks.blockFence, 1);
                registerCustomWoodRecipe(AgriCraftBlocks.blockFence, 8, true, "fff", "fwf", "fff", 'w', REFERENCE, 'f', net.minecraft.init.Blocks.oak_fence);
                registerCustomWoodRecipe(AgriCraftBlocks.blockFence, 8, true, "fff", "fwf", "fff", 'w', REFERENCE, 'f', net.minecraft.init.Blocks.birch_fence);
                registerCustomWoodRecipe(AgriCraftBlocks.blockFence, 8, true, "fff", "fwf", "fff", 'w', REFERENCE, 'f', net.minecraft.init.Blocks.jungle_fence);
                registerCustomWoodRecipe(AgriCraftBlocks.blockFence, 8, true, "fff", "fwf", "fff", 'w', REFERENCE, 'f', net.minecraft.init.Blocks.spruce_fence);
                registerCustomWoodRecipe(AgriCraftBlocks.blockFence, 8, true, "fff", "fwf", "fff", 'w', REFERENCE, 'f', net.minecraft.init.Blocks.acacia_fence);
                registerCustomWoodRecipe(AgriCraftBlocks.blockFence, 8, true, "fff", "fwf", "fff", 'w', REFERENCE, 'f', net.minecraft.init.Blocks.dark_oak_fence);
                registerCustomWoodRecipe(AgriCraftBlocks.blockFenceGate, 1, true, "fwf", 'w', REFERENCE, 'f', fence);
            }
            if (AgriCraftBlocks.enableGrates) {
                registerCustomWoodRecipe(AgriCraftBlocks.blockGrate, 8, true, "w w", " w ", "w w", 'w', REFERENCE);
            }
        }
    }

    private static boolean initWoodList() {
        if(woodList != null && woodList.size()>0) {
            return true;
        }
        for(Field field:AgriCraftBlocks.class.getDeclaredFields()) {
            try {
                if (field.get(null) == null) {
                    continue;
                }
                Object obj = field.get(null);
                if (!(obj instanceof BlockCustomWood)) {
                    continue;
                }
                woodList = new ArrayList<>();
                (((ItemBlockCustomWood) Item.getItemFromBlock((BlockCustomWood) obj))).getSubItems(woodList);
                return true;
            } catch(Exception e) {
                AgriCore.getLogger("AgriCraft").trace(e);
            }
        }
        return false;
    }

    /**
     * Adds the given recipe for every available WOOD type.
     * @param params Same as for GameRegistry. The only difference is that planks will get replaced with the different woods.
     */
    public static void registerCustomWoodRecipe(Block block, int stackSize, boolean shaped, Object... params) {
        for (ItemStack stack : woodList) {
            if(stack.hasTagCompound() && stack.getTagCompound().hasKey(AgriCraftNBT.MATERIAL) && stack.getTagCompound().hasKey(AgriCraftNBT.MATERIAL_META)) {
                //get MATERIAL
                NBTTagCompound materialTag = stack.getTagCompound();
                ItemStack plank = new ItemStack(Block.getBlockFromName(materialTag.getString(AgriCraftNBT.MATERIAL)), 1, materialTag.getInteger(AgriCraftNBT.MATERIAL_META));
                Object[] ingredients = Arrays.copyOf(params, params.length);
                for (int i = 0; i < ingredients.length; i++) {
                    // replace all planks with the custom ones
                    if (ingredients[i] instanceof ItemStack && ((ItemStack) ingredients[i]).isItemEqual(REFERENCE)) {
                        ingredients[i] = plank;
                    }
                    // Also replace ItemBlockCustomWood with the correct version
                    if (ingredients[i] instanceof ItemStack && ingredients[i]!=null && ((ItemStack)ingredients[i]).getItem() instanceof ItemBlockCustomWood) {
                        ((ItemStack) ingredients[i]).setTagCompound(materialTag);
                    }
                }
                ItemStack result = new ItemStack(block, stackSize);
                result.setTagCompound(materialTag);
                //register recipes
                if (shaped)
                    GameRegistry.addShapedRecipe(result, ingredients);
                else
                    addShapelessCustomWoodRecipe(result, ingredients);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void registerCustomWoodRecipe(IRecipe recipe) {
        if(recipe instanceof ShapedRecipes) {
            ShapedRecipes shapedRecipe = (ShapedRecipes) recipe;
            registerCustomWoodRecipe(((ItemBlock) shapedRecipe.getRecipeOutput().getItem()).block, shapedRecipe.getRecipeOutput().stackSize, true, (Object[]) shapedRecipe.recipeItems);
        }
        else if (recipe instanceof ShapelessRecipes) {
            ShapelessRecipes shapelessRecipe = (ShapelessRecipes) recipe;
            registerCustomWoodRecipe(((ItemBlock) shapelessRecipe.getRecipeOutput().getItem()).block, shapelessRecipe.getRecipeOutput().stackSize, false, (Object[]) shapelessRecipe.recipeItems.toArray(new ItemStack[shapelessRecipe.recipeItems.size()]));
        }
    }

    @SuppressWarnings("unchecked")
    private static void addShapelessCustomWoodRecipe(ItemStack output, Object... params) {
        List recipeItemsCopy = new ArrayList();
        for (Object recipeItem : params) {
            if (recipeItem instanceof ItemStack) {
                recipeItemsCopy.add(((ItemStack) recipeItem).copy());
            } else if (recipeItem instanceof Item) {
                recipeItemsCopy.add(new ItemStack((Item) recipeItem));
            } else {
                if (!(recipeItem instanceof Block)) {
                    throw new RuntimeException("Invalid shapeless recipe!");
                }
                recipeItemsCopy.add(new ItemStack((Block) recipeItem));
            }
        }
        RecipeShapelessCustomWood recipe = new RecipeShapelessCustomWood(output, recipeItemsCopy);
        GameRegistry.addRecipe(recipe);
    }
}
