package com.InfinityRaider.AgriCraft.compatibility.computercraft;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.IMethod;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodAnalyze;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodGetBaseBlock;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodGetBaseBlockType;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodGetBrightnessRange;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodGetCurrentSoil;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodGetGrowthStage;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodGetNeededSoil;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodGetPlant;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodGetSpecimen;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodGetSpecimenStats;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodGetStatsFromCrop;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodHasJournal;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodHasPlant;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodHasWeeds;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodIsAnalyzed;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodIsCrossCrop;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodIsFertile;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodIsMature;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodIsSpecimenAnalyzed;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodNeedsBaseBlock;
import com.InfinityRaider.AgriCraft.reference.Names;

import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class ComputerCraftHelper extends ModHelper {
    public static IMethod[] getMethods() {
        return new IMethod[] {
                new MethodAnalyze(),
                new MethodGetBaseBlock(),
                new MethodGetBaseBlockType(),
                new MethodGetBaseBlockType(),
                new MethodGetBrightnessRange(),
                new MethodGetCurrentSoil(),
                new MethodGetGrowthStage(),
                new MethodGetNeededSoil(),
                new MethodGetPlant(),
                new MethodGetSpecimen(),
                new MethodGetSpecimenStats(),
                new MethodGetStatsFromCrop(),
                new MethodHasJournal(),
                new MethodHasPlant(),
                new MethodHasWeeds(),
                new MethodIsAnalyzed(),
                new MethodIsCrossCrop(),
                new MethodIsFertile(),
                new MethodIsMature(),
                new MethodIsSpecimenAnalyzed(),
                new MethodNeedsBaseBlock()
        };
    }

    public static void registerPeripheralProvider(IPeripheralProvider provider) {
        dan200.computercraft.api.ComputerCraftAPI.registerPeripheralProvider(provider);
    }

    @Override
    protected String modId() {
        return Names.Mods.computerCraft;
    }
}
