package com.infinityraider.agricraft.api.v1.requirement;

import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiFunction;

public interface IAgriSeasonLogic {
    Optional<AgriSeason> getSeason(World world, BlockPos pos);

    @Nullable
    IAgriPlugin getOwner();

    void claim(IAgriPlugin plugin, BiFunction<World, BlockPos, Optional<AgriSeason>> getter);
}
