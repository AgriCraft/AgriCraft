package com.infinityraider.agricraft.util;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Predicate;

public final class FluidPredicates {

    public static final Predicate<Fluid> ANY_FLUID = fluid -> true;

    public static final Predicate<Fluid> NOT_LAVA = fluid -> !fluid.equals(Fluids.LAVA);

}
