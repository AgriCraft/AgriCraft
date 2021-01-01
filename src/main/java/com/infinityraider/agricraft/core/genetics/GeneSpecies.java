package com.infinityraider.agricraft.core.genetics;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.api.v1.genetics.IAllel;
import com.infinityraider.agricraft.api.v1.genetics.IMutator;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.core.mutation.AgriMutationHandler;
import com.infinityraider.agricraft.core.plant.NoPlant;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.stream.Collectors;

public class GeneSpecies implements IAgriGene<IAgriPlant> {
    private static final GeneSpecies INSTANCE = new GeneSpecies();

    public static GeneSpecies getInstance() {
        return INSTANCE;
    }

    private GeneSpecies() {}

    @Override
    public IAgriPlant defaultAllel() {
        return NoPlant.getInstance();
    }

    @Override
    public IAllel<IAgriPlant> getAllel(IAgriPlant value) {
        return null;
    }

    @Override
    public IAgriPlant readAllelFromNBT(CompoundNBT tag) {
        return AgriApi.getPlantRegistry().get(tag.getString("agri_plant")).orElse(this.defaultAllel());
    }

    @Override
    public Set<IAllel<IAgriPlant>> allAlleles() {
        return AgriApi.getPlantRegistry().all().stream().map(plant -> (IAllel<IAgriPlant>) plant).collect(Collectors.toSet());
    }

    @Override
    public IMutator<IAgriPlant> mutator() {
        return AgriMutationHandler.getInstance().getActivePlantMutator();
    }

    @Override
    public IAgriGenePair<IAgriPlant> generateGenePair(IAllel<IAgriPlant> first, IAllel<IAgriPlant> second) {
        return new AgriGenePair<>(this, first, second);
    }

    @Nonnull
    @Override
    public String getId() {
        return "agri_species";
    }
}
