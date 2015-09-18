package com.InfinityRaider.AgriCraft.compatibility;

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.compatibility.NEI.NEIHelper;
import com.InfinityRaider.AgriCraft.compatibility.adventofascension.AdventOfAscensionHelper;
import com.InfinityRaider.AgriCraft.compatibility.applecore.AppleCoreHelper;
import com.InfinityRaider.AgriCraft.compatibility.applemilktea.AppleMilkTeaHelper;
import com.InfinityRaider.AgriCraft.compatibility.arsmagica.ArsMagicaHelper;
import com.InfinityRaider.AgriCraft.compatibility.biomesoplenty.BiomesOPlentyHelper;
import com.InfinityRaider.AgriCraft.compatibility.bloodmagic.BloodMagicHelper;
import com.InfinityRaider.AgriCraft.compatibility.bluepower.BluePowerHelper;
import com.InfinityRaider.AgriCraft.compatibility.botania.BotaniaHelper;
import com.InfinityRaider.AgriCraft.compatibility.chococraft.ChocoCraftHelper;
import com.InfinityRaider.AgriCraft.compatibility.ex_nihilo.ExNihiloHelper;
import com.InfinityRaider.AgriCraft.compatibility.extrabiomesxl.ExtraBiomesXLHelper;
import com.InfinityRaider.AgriCraft.compatibility.forestry.ForestryHelper;
import com.InfinityRaider.AgriCraft.compatibility.gardenstuff.GardenStuffHelper;
import com.InfinityRaider.AgriCraft.compatibility.growthcraft.GrowthCraftRiceHelper;
import com.InfinityRaider.AgriCraft.compatibility.harvestcraft.HarvestcraftHelper;
import com.InfinityRaider.AgriCraft.compatibility.hungeroverhaul.HungerOverhaulHelper;
import com.InfinityRaider.AgriCraft.compatibility.immersiveengineering.ImmersiveEngineeringHelper;
import com.InfinityRaider.AgriCraft.compatibility.lordoftherings.LordOfTheRingsHelper;
import com.InfinityRaider.AgriCraft.compatibility.magicalcrops.MagicalCropsHelper;
import com.InfinityRaider.AgriCraft.compatibility.minefactoryreloaded.MFRHelper;
import com.InfinityRaider.AgriCraft.compatibility.minetweaker.MinetweakerHelper;
import com.InfinityRaider.AgriCraft.compatibility.mobdropcrops.MobDropCropsHelper;
import com.InfinityRaider.AgriCraft.compatibility.natura.NaturaHelper;
import com.InfinityRaider.AgriCraft.compatibility.plantmegapack.PlantMegaPackHelper;
import com.InfinityRaider.AgriCraft.compatibility.psychedelicraft.PsychedelicraftHelper;
import com.InfinityRaider.AgriCraft.compatibility.rotarycraft.RotaryCraftHelper;
import com.InfinityRaider.AgriCraft.compatibility.tconstruct.TinkersConstructHelper;
import com.InfinityRaider.AgriCraft.compatibility.thaumcraft.ThaumcraftHelper;
import com.InfinityRaider.AgriCraft.compatibility.waila.WailaHelper;
import com.InfinityRaider.AgriCraft.compatibility.weeeflowers.WeeeFlowersHelper;
import com.InfinityRaider.AgriCraft.compatibility.witchery.WitcheryHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

import cpw.mods.fml.common.Loader;

public abstract class ModHelper {
    /** HashMap holding all ModHelpers, with the respective mod id as key */
    private static final HashMap<String, ModHelper> modHelpers = new HashMap<String, ModHelper>();
    /** HashMap holding all custom tools, with the correct mod helper as value */
    private static final HashMap<Item, ModHelper> modTools = new HashMap<Item, ModHelper>();

    /** Method to create only one instance for each mod helper */
    private static ModHelper createInstance(Class<? extends ModHelper> clazz) {
        ModHelper helper = null;
        try {
            helper = clazz.newInstance();
        } catch (Exception e) {
            if(ConfigurationHandler.debug) {
                e.printStackTrace();
            }
        }
        if(helper!=null) {
            modHelpers.put(helper.modId(), helper);
        }
        return helper;
    }

    /** Checks if integration for this mod id is allowed, meaning the mod is present, and integration is allowed in the config */
    public static boolean allowIntegration(String modId) {
        ModHelper helper = modHelpers.get(modId);
        if(helper != null ) {
            return helper.allowIntegration();
        } else {
            return Loader.isModLoaded(modId) && ConfigurationHandler.enableModCompatibility(modId);
        }
    }

    /** Checks if integration for this mod id is allowed, meaning the mod is present, and integration is allowed in the config */
    public final boolean allowIntegration() {
        String id =this.modId();
        return Loader.isModLoaded(id) && ConfigurationHandler.enableModCompatibility(id);
    }

    /** Checks if this item has custom behaviour when used on crops */
    public static boolean isRightClickHandled(Item tool) {
        return modTools.containsKey(tool);
    }

    /**
     * static method, called when the item contained in the ItemStack has custom behaviour when used on crops.
     * delegates the call to useTool(World world, int x, int y, int z, EntityPlayer player, ItemStack stack, BlockCrop block, TileEntityCrop crop) on the correct ModHelper.
     *
     * @param world the World object for the crop
     * @param x the x-coordinate for the crop
     * @param y the y-coordinate for the crop
     * @param z the z-coordinate for the crop
     * @param player the EntityPlayer object interacting with the crop, might be null if done trough automation
     * @param stack the ItemStack holding the Item
     * @param block the BlockCrop instance
     * @param crop the TileEntity being interacted with
     *
     * @return true to consume the right click
     */
    public static boolean handleRightClickOnCrop(World world, int x, int y, int z, EntityPlayer player, ItemStack stack, BlockCrop block, TileEntityCrop crop) {
        return isRightClickHandled(stack.getItem()) && modTools.get(stack.getItem()).useTool(world, x, y, z, player, stack, block, crop);
    }

    /**
     * called when the item contained in the ItemStack has custom behaviour when used on crops
     *
     * @param world the World object for the crop
     * @param x the x-coordinate for the crop
     * @param y the y-coordinate for the crop
     * @param z the z-coordinate for the crop
     * @param player the EntityPlayer object interacting with the crop, might be null if done trough automation
     * @param stack the ItemStack holding the Item
     * @param block the BlockCrop instance
     * @param crop the TileEntity being interacted with
     *
     * @return true to consume the right click
     */
    protected boolean useTool(World world, int x, int y, int z, EntityPlayer player, ItemStack stack, BlockCrop block, TileEntityCrop crop) {
        return false;
    }

    /** returns a List containing every Item that should have custom behaviour when used on crops */
    protected List<Item> getTools() {
        return null;
    }

    /** called during the initialization phase of FML's mod loading cycle */
    protected void init() {}


    /** called during the post-initialization phase of FML's mod loading cycle to register all CropPlants for this mod*/
    protected  void initPlants() {};

    /** called during the post-initialization phase of FML's mod loading cycle */
    protected void postTasks() {}

    /** returns the mod id for this mod */
    protected abstract String modId();

    /** calls the init() method for all mod helpers which have their mod loaded and compatibility enabled */
    public static void initHelpers() {
        for(ModHelper helper:modHelpers.values()) {
            String id = helper.modId();
            boolean flag = Loader.isModLoaded(id) && ConfigurationHandler.enableModCompatibility(id);
            if(flag) {
                helper.init();
            }
        }
    }

    /** calls the initPlants() method for all mod helpers which have their mod loaded and compatibility enabled */
    public static void initModPlants() {
        for(ModHelper helper:modHelpers.values()) {
            String id = helper.modId();
            boolean flag = Loader.isModLoaded(id) && ConfigurationHandler.enableModCompatibility(id);
            if(flag) {
                helper.initPlants();
            }
        }
    }

    /** calls the postInit() method for all mod helpers which have their mod loaded and compatibility enabled */
    public static void postInit() {
        for (ModHelper helper : modHelpers.values()) {
            String id = helper.modId();
            boolean flag = Loader.isModLoaded(id) && ConfigurationHandler.enableModCompatibility(id);
            if(flag) {
                helper.postTasks();
                List<Item> tools = helper.getTools();
                if(tools != null) {
                    for(Item tool:tools) {
                        if(tool!=null) {
                            modTools.put(tool, helper);
                        }
                    }
                }
            }
        }
    }

    /** method holding all ModHelper classes */
    public static void findHelpers() {
        Class[] classes = {
                AdventOfAscensionHelper.class,
                AppleCoreHelper.class,
                AppleMilkTeaHelper.class,
                ArsMagicaHelper.class,
                BiomesOPlentyHelper.class,
                BloodMagicHelper.class,
                BluePowerHelper.class,
                BotaniaHelper.class,
                ChocoCraftHelper.class,
                ExNihiloHelper.class,
                ExtraBiomesXLHelper.class,
                ForestryHelper.class,
                GardenStuffHelper.class,
                GrowthCraftRiceHelper.class,
                HarvestcraftHelper.class,
                HungerOverhaulHelper.class,
                ImmersiveEngineeringHelper.class,
                LordOfTheRingsHelper.class,
                MagicalCropsHelper.class,
                MFRHelper.class,
                MinetweakerHelper.class,
                MobDropCropsHelper.class,
                NaturaHelper.class,
                NEIHelper.class,
                PlantMegaPackHelper.class,
                //PneumaticCraftHelper.class,
                PsychedelicraftHelper.class,
                RotaryCraftHelper.class,
                ThaumcraftHelper.class,
                TinkersConstructHelper.class,
                WailaHelper.class,
                WeeeFlowersHelper.class,
                WitcheryHelper.class
        };
        for(Class clazz:classes) {
            if(ModHelper.class.isAssignableFrom(clazz)) {
                createInstance(clazz);
            }
        }
    }
}
