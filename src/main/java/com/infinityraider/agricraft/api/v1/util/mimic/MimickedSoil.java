package com.infinityraider.agricraft.api.v1.util.mimic;

import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Utility class, extend to override some selected behaviour of existing soils
 */
public class MimickedSoil implements IAgriSoil {
    private final IAgriSoil original;

    protected MimickedSoil(IAgriSoil original) {
        this.original = original;
    }

    protected final IAgriSoil getOriginal() {
        return this.original;
    }

    @Nonnull
    @Override
    public String getId() {
        return this.getOriginal().getId();
    }

    @Nonnull
    @Override
    public Component getName() {
        return this.getOriginal().getName();
    }

    @Nonnull
    @Override
    public Collection<BlockState> getVariants() {
        return this.getOriginal().getVariants();
    }

    @Nonnull
    @Override
    public Humidity getHumidity() {
        return this.getOriginal().getHumidity();
    }

    @Nonnull
    @Override
    public Acidity getAcidity() {
        return this.getOriginal().getAcidity();
    }

    @Nonnull
    @Override
    public Nutrients getNutrients() {
        return this.getOriginal().getNutrients();
    }

    @Override
    public double getGrowthModifier() {
        return this.getOriginal().getGrowthModifier();
    }

    @Override
    public boolean isSoil() {
        return this.getOriginal().isSoil();
    }
}
