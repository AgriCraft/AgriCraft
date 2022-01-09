package com.infinityraider.agricraft.impl.v1.journal;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthRequirement;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.capability.CapabilityResearchedPlants;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.network.MessagePlantResearched;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PlantPage implements IAgriJournalItem.IPage {
    public static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "plant_page");

    private final IAgriPlant plant;
    private final List<IAgriPlant> all;
    private final List<IAgriGrowthStage> stages;

    private final boolean[] brightnessMask;
    private final boolean[] humidityMask;
    private final boolean[] acidityMask;
    private final boolean[] nutrientsMask;
    private final boolean[] seasonMask;

    private final List<ItemStack> drops;
    private final List<List<IAgriPlant>> mutationsOnPage;
    private final List<List<IAgriPlant>> mutationsOffPage;

    public PlantPage(IAgriPlant plant, List<IAgriPlant> all) {
        this.plant = plant;
        this.all = all;
        this.stages = plant.getGrowthStages().stream()
                .sorted(Comparator.comparingDouble(IAgriGrowthStage::growthPercentage))
                .collect(Collectors.toList());
        this.brightnessMask = new boolean[16];
        IAgriGrowthRequirement req = this.plant.getGrowthRequirement(this.plant.getInitialGrowthStage());
        for(int light = 0; light < this.brightnessMask.length; light++) {
            this.brightnessMask[light] = req.getLightLevelResponse(light, 1).isFertile();
        }
        this.humidityMask = new boolean[IAgriSoil.Humidity.values().length - 1];
        for(int humidity = 0; humidity < this.humidityMask.length; humidity++) {
            this.humidityMask[humidity] = req.getSoilHumidityResponse(IAgriSoil.Humidity.values()[humidity], 1).isFertile();
        }
        this.acidityMask = new boolean[IAgriSoil.Acidity.values().length  - 1];
        for(int acidity = 0; acidity < this.acidityMask.length; acidity++) {
            this.acidityMask[acidity] = req.getSoilAcidityResponse(IAgriSoil.Acidity.values()[acidity], 1).isFertile();
        }
        this.nutrientsMask = new boolean[IAgriSoil.Nutrients.values().length - 1];
        for(int nutrients = 0; nutrients < this.nutrientsMask.length; nutrients++) {
            this.nutrientsMask[nutrients] = req.getSoilNutrientsResponse(IAgriSoil.Nutrients.values()[nutrients], 1).isFertile();
        }
        this.seasonMask = new boolean[AgriSeason.values().length - 1];
        for(int season = 0; season < this.seasonMask.length; season++) {
            this.seasonMask[season] = req.getSeasonResponse(AgriSeason.values()[season], 1).isFertile();
        }
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        this.plant.getAllPossibleProducts(builder::add);
        this.drops = builder.build();
        List<List<IAgriPlant>> mutations = Stream.concat(
                this.gatherMutationSprites(mutation -> mutation.hasParent(this.plant)),
                this.gatherMutationSprites(mutation -> mutation.hasChild(this.plant))
        ).collect(Collectors.toList());
        int count = mutations.size();
        if(count <= 6) {
            this.mutationsOnPage = mutations.subList(0, count);
            this.mutationsOffPage = ImmutableList.of();
        } else {
            this.mutationsOnPage = mutations.subList(0, 6);
            this.mutationsOffPage = mutations.subList(6, count);
        }
    }

    @Nonnull
    @Override
    public ResourceLocation getDataDrawerId() {
        return ID;
    }

    public IAgriPlant getPlant() {
        return this.plant;
    }

    public List<IAgriPlant> getAllDiscoveredPlants() {
        return this.all;
    }

    public List<IAgriGrowthStage> getStages() {
        return this.stages;
    }

    public boolean[] brightnessMask() {
        return this.brightnessMask;
    }

    public boolean[] humidityMask() {
        return this.humidityMask;
    }

    public boolean[] acidityMask() {
        return this.acidityMask;
    }

    public boolean[] nutrientsMask() {
        return this.nutrientsMask;
    }

    public boolean[] seasonMask() {
        return this.seasonMask;
    }

    public List<ItemStack> getDrops() {
        return this.drops;
    }

    public List<List<IAgriPlant>> getOnPageMutations() {
        return this.mutationsOnPage;
    }

    public List<List<IAgriPlant>> getOffPageMutations() {
        return this.mutationsOffPage;
    }
    @Override
    public void onPageOpened(PlayerEntity player, ItemStack stack, IAgriJournalItem journal) {
        // TODO
        if(!CapabilityResearchedPlants.getInstance().isPlantResearched(AgriCraft.instance.getClientPlayer(), this.plant)) {
            new MessagePlantResearched(this.plant).sendToServer();
        }
    }

    protected Stream<List<IAgriPlant>> gatherMutationSprites(Predicate<IAgriMutation> filter) {
        return AgriApi.getMutationRegistry().stream()
                .filter(filter).map(mutation ->
                        Stream.of(mutation.getParents().get(0), mutation.getParents().get(1), mutation.getChild())
                                .map(plant -> {
                                    if (this.isPlantKnown(plant)) {
                                        return plant;
                                    } else {
                                        return NoPlant.getInstance();
                                    }
                                })
                                .collect(Collectors.toList()));
    }

    protected boolean isPlantKnown(IAgriPlant plant) {
        if(AgriCraft.instance.getConfig().progressiveJEI()) {
            return all.contains(plant)
                    || CapabilityResearchedPlants.getInstance().isPlantResearched(AgriCraft.instance.getClientPlayer(), plant);
        }
        return true;
    }

}
