package com.InfinityRaider.AgriCraft.compatibility;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.compatibility.NEI.NEIHelper;
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
import com.InfinityRaider.AgriCraft.compatibility.magicalcrops.MagicalCropsHelper;
import com.InfinityRaider.AgriCraft.compatibility.minefactoryreloaded.MFRHelper;
import com.InfinityRaider.AgriCraft.compatibility.minetweaker.MinetweakerHelper;
import com.InfinityRaider.AgriCraft.compatibility.mobdropcrops.MobDropCropsHelper;
import com.InfinityRaider.AgriCraft.compatibility.natura.NaturaHelper;
import com.InfinityRaider.AgriCraft.compatibility.plantmegapack.PlantMegaPackHelper;
import com.InfinityRaider.AgriCraft.compatibility.pneumaticcraft.PneumaticCraftHelper;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;

public abstract class ModHelper {
    private static final HashMap<String, ModHelper> modHelpers = new HashMap<String, ModHelper>();
    private static final HashMap<Item, ModHelper> modTools = new HashMap<Item, ModHelper>();

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

    public static boolean allowIntegration(String modId) {
        ModHelper helper = modHelpers.get(modId);
        return helper != null && helper.allowIntegration();
    }

    public final boolean allowIntegration() {
        String id =this.modId();
        return Loader.isModLoaded(id) && ConfigurationHandler.enableModCompatibility(id);
    }

    public static boolean isRightClickHandled(Item tool) {
        return modTools.containsKey(tool);
    }

    public static boolean handleRightClickOnCrop(World world, int x, int y, int z, EntityPlayer player, ItemStack stack, BlockCrop block, TileEntityCrop crop) {
        if(isRightClickHandled(stack.getItem())) {
            return modTools.get(stack.getItem()).useTool(world, x, y, z, player, stack, block, crop);
        }
        return false;
    }

    protected boolean useTool(World world, int x, int y, int z, EntityPlayer player, ItemStack stack, BlockCrop block, TileEntityCrop crop) {
        return false;
    }

    protected List<Item> getTools() {
        return null;
    }

    protected abstract void init();

    protected abstract void initPlants();

    protected void postTasks() {}

    protected abstract String modId();

    public static void initHelpers() {
        findHelpers();
        for(ModHelper helper:modHelpers.values()) {
            String id = helper.modId();
            boolean flag = Loader.isModLoaded(id) && ConfigurationHandler.enableModCompatibility(id);
            if(flag) {
                helper.init();
            }
        }
    }

    public static void initModPlants() {
        for(ModHelper helper:modHelpers.values()) {
            String id = helper.modId();
            boolean flag = Loader.isModLoaded(id) && ConfigurationHandler.enableModCompatibility(id);
            if(flag) {
                helper.initPlants();
            }
        }
    }

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

    private static void findHelpers() {
        Class[] classes = {
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
                MagicalCropsHelper.class,
                MFRHelper.class,
                MinetweakerHelper.class,
                MobDropCropsHelper.class,
                NaturaHelper.class,
                NEIHelper.class,
                PlantMegaPackHelper.class,
                PneumaticCraftHelper.class,
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
