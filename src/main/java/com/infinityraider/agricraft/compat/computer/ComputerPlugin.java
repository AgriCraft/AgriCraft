/*
 */
package com.infinityraider.agricraft.compat.computer;

import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.compat.computer.methods.MethodAnalyze;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetBaseBlock;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetBrightness;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetBrightnessRange;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetCurrentSoil;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetGrowthStage;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetNeededSoil;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetPlant;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetSpecimen;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetStats;
import com.infinityraider.agricraft.compat.computer.methods.MethodHasJournal;
import com.infinityraider.agricraft.compat.computer.methods.MethodHasPlant;
import com.infinityraider.agricraft.compat.computer.methods.MethodIsAnalyzed;
import com.infinityraider.agricraft.compat.computer.methods.MethodIsCrossCrop;
import com.infinityraider.agricraft.compat.computer.methods.MethodIsFertile;
import com.infinityraider.agricraft.compat.computer.methods.MethodIsMature;
import com.infinityraider.agricraft.compat.computer.methods.MethodNeedsBaseBlock;
import com.infinityraider.agricraft.api.v1.misc.IAgriPeripheralMethod;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import javax.annotation.Nonnull;

/**
 *
 *
 */
@AgriPlugin
public class ComputerPlugin implements IAgriPlugin {

    @Nonnull
    private static final IAgriPeripheralMethod[] METHODS = new IAgriPeripheralMethod[]{
        new MethodAnalyze(),
        new MethodGetBaseBlock(),
        new MethodGetBrightness(),
        new MethodGetBrightnessRange(),
        new MethodGetCurrentSoil(),
        new MethodGetGrowthStage(),
        new MethodGetNeededSoil(),
        new MethodGetPlant(),
        new MethodGetSpecimen(),
        new MethodGetStats(),
        new MethodHasJournal(),
        new MethodHasPlant(),
        new MethodIsAnalyzed(),
        new MethodIsCrossCrop(),
        new MethodIsFertile(),
        new MethodIsMature(),
        new MethodNeedsBaseBlock()
    };

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getId() {
        return "computer";
    }

    @Override
    public String getName() {
        return "Computer Integration";
    }

    @Override
    public void registerPeripheralMethods(IAgriRegistry<IAgriPeripheralMethod> methodRegistry) {
        // Register all the default methods.
        for (IAgriPeripheralMethod m : METHODS) {
            methodRegistry.add(m);
        }
    }

}
