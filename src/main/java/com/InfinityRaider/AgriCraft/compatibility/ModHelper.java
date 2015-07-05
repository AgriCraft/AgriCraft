package com.InfinityRaider.AgriCraft.compatibility;

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
import com.InfinityRaider.AgriCraft.compatibility.thaumcraft.ThaumcraftHelper;
import com.InfinityRaider.AgriCraft.compatibility.waila.WailaHelper;
import com.InfinityRaider.AgriCraft.compatibility.weeeflowers.WeeeFlowersHelper;
import com.InfinityRaider.AgriCraft.compatibility.witchery.WitcheryHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import cpw.mods.fml.common.Loader;

import java.util.HashMap;

public abstract class ModHelper {
    private static final HashMap<String, ModHelper> modHelpers = new HashMap<String, ModHelper>();

    public static ModHelper createInstance(Class<? extends ModHelper> clazz) {
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
