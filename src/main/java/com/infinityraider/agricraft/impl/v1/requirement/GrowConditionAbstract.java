package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowCondition;
import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

public abstract class GrowConditionAbstract implements IAgriGrowCondition {
    private final int strength;
    private final RequirementType type;
    private final ImmutableSet<BlockPos> offsets;
    private final CacheType cacheType;

    public GrowConditionAbstract(int strength, RequirementType type, CacheType cacheType) {
        this(strength, type, ImmutableSet.of(), cacheType);
    }

    public GrowConditionAbstract(int strength, RequirementType type, BlockPos offset, CacheType cacheType) {
        this(strength, type, ImmutableSet.of(offset), cacheType);
    }

    public GrowConditionAbstract(int strength, RequirementType type, ImmutableSet<BlockPos> offsets, CacheType cacheType) {
        this.strength = strength;
        this.type = type;
        this.offsets = offsets;
        this.cacheType = cacheType;
    }

    @Override
    public final RequirementType getType() {
        return this.type;
    }

    public final int strengthToIgnore() {
        return this.strength;
    }

    @Override
    public boolean isMet(@Nonnull World world, @Nonnull BlockPos pos, int strength) {
        return strength >= this.strengthToIgnore() || this.isMet(world, pos);
    }

    public abstract boolean isMet(@Nonnull World world, @Nonnull BlockPos pos);

    @Override
    public final ImmutableSet<BlockPos> offsetsToCheck() {
        return this.offsets;
    }

    @Override
    public void addDescription(@Nonnull Consumer<ITextComponent> consumer) {
        consumer.accept(getTooltip(this.getType()));
    }

    @Override
    public int getComplexity() {
        return this.offsetsToCheck().size();
    }

    @Override
    public CacheType getCacheType() {
        return this.cacheType;
    }

    private static final Map<RequirementType, ITextComponent> tooltips = Maps.newEnumMap(RequirementType.class);

    private static ITextComponent getTooltip(RequirementType type) {
        ITextComponent tooltip = tooltips.get(type);
        if(tooltip == null) {
            tooltip = new TranslationTextComponent(AgriCraft.instance.getModId() + ".tooltip.req." + type.name().toLowerCase());
            tooltips.put(type, tooltip);
        }
        return tooltip;
    }
}
