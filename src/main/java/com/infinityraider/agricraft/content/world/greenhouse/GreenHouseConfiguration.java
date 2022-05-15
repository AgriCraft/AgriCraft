package com.infinityraider.agricraft.content.world.greenhouse;

import com.infinityraider.agricraft.AgriCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ChunkPos;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class GreenHouseConfiguration {
    private final Set<Tuple<ChunkPos, Map<BlockPos, GreenHousePart.Block>>> parts;
    private final GreenHouseProperties properties;

    public GreenHouseConfiguration(Set<Tuple<ChunkPos, Map<BlockPos, GreenHousePart.Block>>> parts, BlockPos min, BlockPos max, int interiorCount, int ceilingCount, int ceilingGlassCount) {
        this.parts = parts;
        this.properties = new GreenHouseProperties(min, max, interiorCount, ceilingCount, ceilingGlassCount);
    }

    public Stream<Tuple<ChunkPos, Map<BlockPos, GreenHousePart.Block>>> parts() {
        return this.parts.stream();
    }

    public GreenHouseProperties getProperties() {
        return this.properties;
    }

    public boolean checkGlassRatio() {
        return this.getCeilingGlassFraction() >= AgriCraft.instance.getConfig().greenHouseCeilingGlassFraction();
    }

    public double getCeilingGlassFraction() {
        return (this.getProperties().getCeilingGlassCount() + 0.0) / this.getProperties().getCeilingCount();
    }
}
