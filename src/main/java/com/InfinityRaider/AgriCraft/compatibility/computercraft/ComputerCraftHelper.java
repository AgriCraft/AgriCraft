package com.InfinityRaider.AgriCraft.compatibility.computercraft;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.*;
import com.InfinityRaider.AgriCraft.reference.Names;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.block.Block;

public class ComputerCraftHelper extends ModHelper {
    public static IMethod[] getMethods() {
        return new IMethod[] {
                new MethodAnalyze(),
                new MethodGetBaseBlock(),
                new MethodGetBaseBlockType(),
                new MethodGetBrightness(),
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

    public static Block getComputerBlock() {
        return (Block) Block.blockRegistry.getObject("ComputerCraft:CC-Computer");
    }

    @Override
    protected String modId() {
        return Names.Mods.computerCraft;
    }
}
