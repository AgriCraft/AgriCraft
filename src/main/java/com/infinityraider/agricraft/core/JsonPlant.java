/*
 * Vanilla Crop Class.
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriPlant;
import com.agricraft.agricore.plant.AgriStack;
import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.api.requirement.IGrowthRequirement;
import com.infinityraider.agricraft.api.render.RenderMethod;
import com.infinityraider.agricraft.api.requirement.BlockCondition;
import com.infinityraider.agricraft.api.requirement.IGrowthReqBuilder;
import com.infinityraider.agricraft.api.util.FuzzyStack;
import com.infinityraider.agricraft.farming.CropPlant;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.utility.BlockRange;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class JsonPlant extends CropPlant {

    public final AgriPlant plant;

    private List<FuzzyStack> seedItems;

    public JsonPlant(AgriPlant plant) {
        super(initGrowthRequirementJSON(plant));
        this.plant = Objects.requireNonNull(plant, "A JSONPlant may not consist of a null AgriPlant! Why would you even try that!?");
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

    @Override
    public Collection<FuzzyStack> getSeedItems() {
        if (this.seedItems == null) {
            this.seedItems = this.plant.getSeedItems().stream()
                    .map(i -> (FuzzyStack) i.toStack())
                    .filter(TypeHelper::isNonNull)
                    .collect(Collectors.toList());
            if (this.seedItems.isEmpty()) {
                this.seedItems.add(new FuzzyStack(new ItemStack(AgriItems.getInstance().AGRI_SEED)));
            }
        }
        return this.seedItems;
    }

    @Override
    public String getInformation() {
        return this.plant.getDescription().toString();
    }

    @Override
    public boolean isWeed() {
        return this.plant.isWeedable();
    }

    @Override
    public boolean isAggressive() {
        return this.plant.isAgressive();
    }

    @Override
    public double getSpreadChance() {
        return this.plant.getSpreadChance();
    }

    @Override
    public double getSpawnChance() {
        return this.plant.getSpawnChance();
    }

    @Override
    public double getGrassDropChance() {
        return this.plant.getGrassDropChance();
    }

    @Override
    public double getGrowthChanceBase() {
        return plant.getGrowthChance();
    }

    @Override
    public double getGrowthChanceBonus() {
        return plant.getGrowthBonus();
    }

    @Override
    public int getGrowthStages() {
        return this.plant.getGrowthStages();
    }

    @Override
    public List<ItemStack> getAllFruits() {
        return this.plant.getProducts().getAll().stream()
                .map(AgriStack::toStack)
                .filter(p -> p instanceof FuzzyStack)
                .map(p -> ((FuzzyStack) p).toStack())
                .collect(Collectors.toList());
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return this.plant.getProducts().getRandom(rand).stream()
                .map(AgriStack::toStack)
                .filter(p -> p instanceof FuzzyStack)
                .map(p -> ((FuzzyStack) p).toStack())
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean isFertilizable() {
        return this.plant.canBonemeal();
    }

    @Override
    public int getTier() {
        return this.plant.getTier();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getHeight(int meta) {
        return Constants.UNIT * 13;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderMethod getRenderMethod() {
        switch (this.plant.getTexture().getRenderType()) {
            default:
            case HASH:
                return RenderMethod.HASHTAG;
            case CROSS:
                return RenderMethod.CROSSED;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getPrimaryPlantTexture(int growthStage) {
        return new ResourceLocation(plant.getTexture().getPlantTexture(growthStage));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getSecondaryPlantTexture(int growthStage) {
        return null;
    }

    @Override
    public ResourceLocation getSeedTexture() {
        return new ResourceLocation(plant.getTexture().getSeedTexture());
    }

    public static final IGrowthRequirement initGrowthRequirementJSON(AgriPlant plant) {

        IGrowthReqBuilder builder = GrowthRequirementHandler.getNewBuilder();

        if (plant == null) {
            return builder.build();
        }

        if (plant.getRequirement().getSoils().isEmpty()) {
            AgriCore.getLogger("AgriCraft").warn("Plant: \"{0}\" has no valid soils to plant on!", plant.getPlantName());
        }

        plant.getRequirement().getSoils().stream()
                .map(JsonSoil::new)
                .forEach(builder::addSoil);

        plant.getRequirement().getConditions().forEach(obj -> {
            final Object stack = obj.toStack();
            if (stack instanceof FuzzyStack) {
                builder.addCondition(
                        new BlockCondition(
                                (FuzzyStack) stack,
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

}
