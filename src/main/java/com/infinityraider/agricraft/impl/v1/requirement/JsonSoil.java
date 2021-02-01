package com.infinityraider.agricraft.impl.v1.requirement;

import com.agricraft.agricore.plant.AgriSoil;
import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
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
    private final AgriSoil soil;
    private final ITextComponent name;
    private final Collection<BlockState> variants;

    @SuppressWarnings("unchecked")
    public JsonSoil(@Nonnull AgriSoil soil) {
        this.soil = Preconditions.checkNotNull(soil);
        this.name = new TranslationTextComponent(soil.getLangKey());
        this.variants = Collections.unmodifiableCollection(Preconditions.checkNotNull(this.soil.getVariants(BlockState.class)));
    }

    @Override
    @Nonnull
    public String getId() {
        return this.soil.getId();
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

}
