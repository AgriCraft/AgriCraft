package com.InfinityRaider.AgriCraft.compatibility;

import com.InfinityRaider.AgriCraft.compatibility.applecore.AppleCoreHelper;
import com.InfinityRaider.AgriCraft.compatibility.arsmagica.ArsMagicaHelper;
import com.InfinityRaider.AgriCraft.compatibility.bloodmagic.BloodMagicHelper;
import com.InfinityRaider.AgriCraft.compatibility.bluepower.BluePowerHelper;
import com.InfinityRaider.AgriCraft.compatibility.botania.BotaniaHelper;
import com.InfinityRaider.AgriCraft.compatibility.chococraft.ChocoCraftHelper;
import com.InfinityRaider.AgriCraft.compatibility.ex_nihilo.ExNihiloHelper;
import com.InfinityRaider.AgriCraft.compatibility.harvestcraft.HarvestcraftHelper;
import com.InfinityRaider.AgriCraft.compatibility.hungeroverhaul.HungerOverhaulHelper;
import com.InfinityRaider.AgriCraft.compatibility.magicalcrops.MagicalCropsHelper;
import com.InfinityRaider.AgriCraft.compatibility.minefactoryreloaded.MFRHelper;
import com.InfinityRaider.AgriCraft.compatibility.minetweaker.MinetweakerHelper;
import com.InfinityRaider.AgriCraft.compatibility.mobdropcrops.MobDropCropsHelper;
import com.InfinityRaider.AgriCraft.compatibility.natura.NaturaHelper;
import com.InfinityRaider.AgriCraft.compatibility.plantmegapack.PlantMegaPackHelper;
import com.InfinityRaider.AgriCraft.compatibility.pneumaticcraft.PneumaticCraftHelper;
import com.InfinityRaider.AgriCraft.compatibility.psychedelicraft.PsychedelicraftHelper;
import com.InfinityRaider.AgriCraft.compatibility.thaumcraft.ThaumcraftHelper;
import com.InfinityRaider.AgriCraft.compatibility.waila.WailaHelper;
import com.InfinityRaider.AgriCraft.compatibility.weeeflowers.WeeeFlowersHelper;
import com.InfinityRaider.AgriCraft.compatibility.witchery.WitcheryHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.Loader;

import java.util.ArrayList;

public abstract class ModHelper {
    private static final ArrayList<ModHelper> modHelpers = new ArrayList<ModHelper>();

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
            modHelpers.add(helper);
        }
        return helper;
    }

    protected abstract void init();

    protected abstract void initPlants();

    protected abstract String modId();

    public static void initHelpers() {
        findHelpers();
        for(ModHelper helper:modHelpers) {
            String id = helper.modId();
            boolean flag = Loader.isModLoaded(id);
            if(flag) {
                LogHelper.debug("Initializing mod support for " + helper.modId());
                helper.init();
            }
        }
    }

    public static void initModPlants() {
        for(ModHelper helper:modHelpers) {
            if(Loader.isModLoaded(helper.modId())) {
                helper.initPlants();
            }
        }
    }

    private static void findHelpers() {
        Class[] classes = {
                AppleCoreHelper.class,
                ArsMagicaHelper.class,
                BloodMagicHelper.class,
                BluePowerHelper.class,
                BotaniaHelper.class,
                ChocoCraftHelper.class,
                ExNihiloHelper.class,
                HarvestcraftHelper.class,
                HungerOverhaulHelper.class,
                MagicalCropsHelper.class,
                MFRHelper.class,
                MinetweakerHelper.class,
                MobDropCropsHelper.class,
                NaturaHelper.class,
                PlantMegaPackHelper.class,
                PneumaticCraftHelper.class,
                PsychedelicraftHelper.class,
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
