package com.infinityraider.agricraft.impl.v1.plant;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriPlant;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;
import com.infinityraider.agricraft.api.v1.genetics.IAllel;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.requirement.IGrowCondition;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.util.BlockRange;
import com.infinityraider.agricraft.impl.v1.requirement.GrowthRequirement;
import com.infinityraider.agricraft.impl.v1.requirement.JsonSoil;
import com.infinityraider.agricraft.impl.v1.genetics.GeneSpecies;
import com.infinityraider.agricraft.impl.v1.genetics.AgriMutationRegistry;
import com.infinityraider.agricraft.init.AgriItemRegistry;
import com.infinityraider.agricraft.reference.AgriNBT;

import java.util.*;
import java.util.function.Consumer;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class JsonPlant implements IAgriPlant {

    private final AgriPlant plant;
    private final GrowthLogic growthLogic;
    private final GrowthRequirement growthRequirement;

    private final List<ItemStack> seedItems;

    public JsonPlant(AgriPlant plant) {
        this.plant = Objects.requireNonNull(plant, "A JSONPlant may not consist of a null AgriPlant! Why would you even try that!?");
        this.growthLogic = initGrowth(plant);
        this.growthRequirement = initGrowthRequirement(plant);
        this.seedItems = initSeedItemsListJSON(plant);
    }

    @Override
    public String getId() {
        return this.plant.getId();
    }

    @Override
    public String getPlantName() {
        return this.plant.getPlantName();
    }

    @Override
    public String getSeedName() {
        return this.plant.getSeedName();
    }

    @Nonnull
    @Override
    public Collection<ItemStack> getSeeds() {
        return this.seedItems;
    }

    @Override
    public boolean isFertilizable(IAgriGrowthStage growthStage, IAgriFertilizer fertilizer) {
        return false;
    }

    @Override
    public double getSpreadChance(IAgriGrowthStage growthStage) {
        return this.plant.getSpreadChance();
    }

    @Override
    public double getGrowthChanceBase(IAgriGrowthStage growthStage) {
        return this.plant.getGrowthChance();
    }

    @Override
    public double getGrowthChanceBonus(IAgriGrowthStage growthStage) {
        return this.plant.getGrowthBonus();
    }

    @Override
    public double getSeedDropChanceBase(IAgriGrowthStage growthStage) {
        return this.plant.getSeedDropChance();
    }

    @Override
    public double getSeedDropChanceBonus(IAgriGrowthStage growthStage) {
        return this.plant.getSeedDropBonus();
    }

    @Nonnull
    @Override
    public IAgriGrowthStage getInitialGrowthStage() {
        return this.growthLogic.initial();
    }

    @Nonnull
    @Override
    public IAgriGrowthStage getGrowthStageAfterHarvest() {
        return this.growthLogic.harvest();
    }

    @Override
    public Set<IAgriGrowthStage> getGrowthStages() {
        return this.growthLogic.all();
    }

    @Override
    public final ItemStack getSeed() {
        ItemStack stack = this.plant.getSeedItems().stream()
                .map(s -> s.toStack(ItemStack.class))
                .findFirst()
                .orElse(new ItemStack(AgriItemRegistry.getInstance().seed, 1, )); //TODO: NBT
        if(!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }
        CompoundNBT tag = stack.getTag();
        tag.putString(AgriNBT.SEED, this.getId());
        // TODO: stats
        stack.setTagCompound(tag);
        return stack;
    }

    @Nonnull
    @Override
    public Set<IGrowCondition> getGrowConditions(IAgriGrowthStage stage) {
        return this.growthRequirement.getConditions();
    }

    @Override
    public Optional<BlockState> asBlockState(IAgriGrowthStage stage) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public String getInformation(IAgriGrowthStage stage) {
        return this.plant.getDescription().toString();
    }

    @Override
    public void getAllPossibleProducts(IAgriGrowthStage stage, @Nonnull Consumer<ItemStack> products) {
        this.plant.getProducts().getAll().stream()
                .map(p -> p.toStack(ItemStack.class))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(products);
    }

    @Override
    public void getHarvestProducts(@Nonnull Consumer<ItemStack> products, @Nonnull IAgriGrowthStage growthStage, @Nonnull IAgriStat stat, @Nonnull Random rand) {
        this.plant.getProducts().getRandom(rand).stream()
                .map(p -> p.toStack(ItemStack.class, rand))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(products);
    }

    public static final List<ItemStack> initSeedItemsListJSON(AgriPlant plant) {
        final List<ItemStack> seeds = new ArrayList<>(plant.getSeedItems().size());
        plant.getSeedItems().stream()
                .map(i -> i.toStack(ItemStack.class))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(seeds::add);
        if (seeds.isEmpty()) {
            CompoundNBT;
            //TODO: NBT
            seeds.add(new ItemStack(AgriItemRegistry.getInstance().seed, 1, ));
        }
        return seeds;
    }

    public static GrowthLogic initGrowth(AgriPlant plant) {

    }

    public static GrowthRequirement initGrowthRequirement(AgriPlant plant) {

        IGrowthReqBuilder builder = new GrowthReqBuilder();

        if (plant == null) {
            return builder.build();
        }

        if (plant.getRequirement().getSoils().isEmpty()) {
            AgriCore.getLogger("agricraft").warn("Plant: \"{0}\" has no valid soils to plant on!", plant.getPlantName());
        }

        plant.getRequirement().getSoils().stream()
                .map(JsonSoil::new)
                .forEach(builder::addSoil);

        plant.getRequirement().getConditions().forEach(obj -> {
            final Optional<FuzzyStack> stack = obj.toStack(FuzzyStack.class);
            if (stack.isPresent()) {
                builder.addCondition(
                        new BlockCondition(
                                stack.get(),
                                new BlockRange(
                                        obj.getMinX(), obj.getMinY(), obj.getMinZ(),
                                        obj.getMaxX(), obj.getMaxY(), obj.getMaxZ()
                                )
                        )
                );
            }
        });

        builder.setMinLight(plant.getRequirement().getMinLight());
        builder.setMaxLight(plant.getRequirement().getMaxLight());

        return builder.build();

    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IAgriPlant) && (this.getId().equals(((IAgriPlant) obj).getId()));
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public IAgriGene<IAgriPlant> gene() {
        return GeneSpecies.getInstance();
    }

    @Override
    public IAgriPlant trait() {
        return this;
    }

    @Override
    public boolean isDominant(IAllel<IAgriPlant> other) {
        return AgriMutationRegistry.getInstance().complexity(this)
                <= AgriMutationRegistry.getInstance().complexity(other.trait());
    }

    @Override
    public CompoundNBT writeToNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("agri_plant", this.getId());
        return tag;
    }
}
