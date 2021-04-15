package com.infinityraider.agricraft.impl.v1.requirement;

import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSeasonLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class SeasonLogic implements IAgriSeasonLogic {
    private static final SeasonLogic INSTANCE = new SeasonLogic();

    public static SeasonLogic getInstance() {
        return INSTANCE;
    }

    private IAgriPlugin owner;
    private BiFunction<World, BlockPos, AgriSeason> getter;

    private SeasonLogic() {
        this.owner = null;
        this.getter = (world, pos) -> AgriSeason.ANY;
    }

    @Override
    public boolean isActive() {
        return this.getOwner() != null;
    }

    @Override
    public AgriSeason getSeason(World world, BlockPos pos) {
        return this.getter.apply(world, pos);
    }

    @Nullable
    @Override
    public IAgriPlugin getOwner() {
        return this.owner;
    }

    @Override
    public void claim(IAgriPlugin plugin, BiFunction<World, BlockPos, AgriSeason> getter) {
        this.owner = plugin;
        this.getter = getter;
    }
}
