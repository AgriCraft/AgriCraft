package com.infinityraider.agricraft.core.requirement;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.requirement.IGrowCondition;
import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

public abstract class GrowConditionAbstract implements IGrowCondition {
    private final int strength;
    private final RequirementType type;
    private final ImmutableSet<BlockPos> offsets;

    public GrowConditionAbstract(int strength, RequirementType type) {
        this(strength, type, ImmutableSet.of());
    }

    public GrowConditionAbstract(int strength, RequirementType type, BlockPos offset) {
        this(strength, type, ImmutableSet.of(offset));
    }

    public GrowConditionAbstract(int strength, RequirementType type, ImmutableSet<BlockPos> offsets) {
        this.strength = strength;
        this.type = type;
        this.offsets = offsets;
    }

    @Override
    public final RequirementType getType() {
        return this.type;
    }

    @Override
    public final int strengthToIgnore() {
        return this.strength;
    }

    @Override
    public final ImmutableSet<BlockPos> offsetsToCheck() {
        return this.offsets;
    }

    @Override
    public int getComplexity() {
        return this.offsetsToCheck().size();
    }

    @Override
    public void addDescription(@Nonnull Consumer<ITextComponent> consumer) {
        consumer.accept(getTooltip(this.getType()));
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
