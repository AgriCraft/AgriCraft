package com.infinityraider.agricraft.impl.v1.genetics;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.api.v1.genetics.IAllele;
import com.infinityraider.agricraft.api.v1.genetics.IMutator;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.stream.Collectors;

public class GeneSpecies implements IAgriGene<IAgriPlant> {
    private static final GeneSpecies INSTANCE = new GeneSpecies();

    public static GeneSpecies getInstance() {
        return INSTANCE;
    }

    private final String id;
    private final ITextComponent descr;

    private GeneSpecies() {
        this.id = "agri_species";
        this.descr = new TranslationTextComponent(AgriCraft.instance.getModId() + ".gene." + this.id);
    }

    @Override
    public IAgriPlant defaultAllele(IAgriPlant plant) {
        return NoPlant.getInstance();
    }

    @Override
    public IAllele<IAgriPlant> getAllele(IAgriPlant value) {
        return value;
    }

    @Override
    public IAgriPlant readAlleleFromNBT(CompoundNBT tag) {
        return AgriApi.getPlantRegistry().get(tag.getString(AgriNBT.PLANT)).orElse(NoPlant.getInstance());
    }

    @Override
    public Set<IAllele<IAgriPlant>> allAlleles() {
        return AgriApi.getPlantRegistry().stream().map(plant -> (IAllele<IAgriPlant>) plant).collect(Collectors.toSet());
    }

    @Override
    public IMutator<IAgriPlant> mutator() {
        return AgriMutationHandler.getInstance().getActivePlantMutator();
    }

    @Override
    public IAgriGenePair<IAgriPlant> generateGenePair(IAllele<IAgriPlant> first, IAllele<IAgriPlant> second) {
        return new AgriGenePair<>(this, first, second);
    }

    @Override
    public ITextComponent getDescription() {
        return this.descr;
    }

    @Nonnull
    @Override
    public String getId() {
        return this.id;
    }
}
