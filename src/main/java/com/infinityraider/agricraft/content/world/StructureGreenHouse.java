package com.infinityraider.agricraft.content.world;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.infinitylib.world.IInfStructure;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.Set;

public class StructureGreenHouse implements IInfStructure {
    private final ResourceLocation id;
    private final Set<ResourceLocation> targets;
    private final Holder<StructureProcessorList> processors;

    public StructureGreenHouse(ResourceLocation id, ResourceLocation target) {
        this(id, target, ProcessorLists.EMPTY);
    }

    public StructureGreenHouse(ResourceLocation id, ResourceLocation target, Holder<StructureProcessorList> processors) {
        this.id = id;
        this.targets = ImmutableSet.of(target);
        this.processors = processors;
    }

    @Override
    public ResourceLocation id() {
        return this.id;
    }

    @Override
    public Set<ResourceLocation> targetPools() {
        return this.targets;
    }

    @Override
    public int weight() {
        return AgriCraft.instance.getConfig().getGreenHouseSpawnWeight();
    }

    @Override
    public Holder<StructureProcessorList> processors() {
        return this.processors;
    }

    @Override
    public StructureTemplatePool.Projection placement() {
        return StructureTemplatePool.Projection.RIGID;
    }
}
