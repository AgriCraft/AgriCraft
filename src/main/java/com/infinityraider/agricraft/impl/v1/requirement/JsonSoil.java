package com.infinityraider.agricraft.impl.v1.requirement;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriSoil;
import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import net.minecraft.block.BlockState;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nonnull;

/**
 * Class wrapping the AgriCore AgriSoil.
 */
public class JsonSoil implements IAgriSoil {
    private final String id;
    private final ITextComponent name;
    private final Collection<BlockState> variants;

    private final Humidity humidity;
    private final Acidity acidity;
    private final Nutrients nutrients;

    private final double growthModifier;

    public JsonSoil(@Nonnull AgriSoil soil) {
        this.id = Preconditions.checkNotNull(soil).getId();
        this.name = new TranslationTextComponent(soil.getLangKey());
        this.variants = Collections.unmodifiableCollection(Preconditions.checkNotNull(soil.getVariants(BlockState.class)));
        this.humidity = IAgriSoil.Humidity.fromString(soil.getHumidity()).orElseGet(() -> {
            AgriCore.getLogger("agricraft").warn(
                    "Soil: \"{0}\" does not have valid humidity defined (\"{1}\"), defaulting to DAMP",
                    soil.getId(), soil.getHumidity());
            return Humidity.DAMP;});
        this.acidity = IAgriSoil.Acidity.fromString(soil.getAcidity()).orElseGet(() -> {
            AgriCore.getLogger("agricraft").warn(
                    "Soil: \"{0}\" does not have valid acidity defined (\"{1}\"), defaulting to NEUTRAL",
                    soil.getId(), soil.getAcidity());
            return Acidity.NEUTRAL;});
        this.nutrients = IAgriSoil.Nutrients.fromString(soil.getNutrients()).orElseGet(() -> {
            AgriCore.getLogger("agricraft").warn(
                    "Soil: \"{0}\" does not have valid nutrients defined (\"{1}\"), defaulting to MEDIUM",
                    soil.getId(), soil.getAcidity());
            return Nutrients.MEDIUM;});
        this.growthModifier = soil.getGrowthModifier();
    }

    @Override
    @Nonnull
    public String getId() {
        return this.id;
    }

    @Override
    @Nonnull
    public ITextComponent getName() {
        return this.name;
    }

    @Override
    @Nonnull
    public Collection<BlockState> getVariants() {
        return this.variants;
    }

    @Nonnull
    @Override
    public Humidity getHumidity() {
        return this.humidity;
    }

    @Nonnull
    @Override
    public Acidity getAcidity() {
        return this.acidity;
    }

    @Nonnull
    @Override
    public Nutrients getNutrients() {
        return this.nutrients;
    }

    @Override
    public double getGrowthModifier() {
        return this.growthModifier;
    }
}
