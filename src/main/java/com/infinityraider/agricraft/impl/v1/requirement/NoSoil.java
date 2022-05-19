package com.infinityraider.agricraft.impl.v1.requirement;

import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.reference.AgriToolTips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

public final class NoSoil implements IAgriSoil {
    private static final IAgriSoil INSTANCE = new NoSoil();

    public static IAgriSoil getInstance() {
        return INSTANCE;
    }

    private final String id;

    private NoSoil() {
        this.id = "none";
    }

    @Nonnull
    @Override
    public String getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public Component getName() {
        return AgriToolTips.UNKNOWN;
    }

    @Nonnull
    @Override
    public Collection<BlockState> getVariants() {
        return Collections.emptySet();
    }

    @Nonnull
    @Override
    public Humidity getHumidity() {
        return Humidity.INVALID;
    }

    @Nonnull
    @Override
    public Acidity getAcidity() {
        return Acidity.INVALID;
    }

    @Nonnull
    @Override
    public Nutrients getNutrients() {
        return Nutrients.INVALID;
    }

    @Override
    public double getGrowthModifier() {
        return 0;
    }

    @Override
    public boolean isSoil() {
        return false;
    }
}
