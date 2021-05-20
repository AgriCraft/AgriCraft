/*
 * Vanilla Crop Class.
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriPlant;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.render.RenderMethod;
import com.infinityraider.agricraft.api.v1.requirement.BlockCondition;
import com.infinityraider.agricraft.api.v1.requirement.IGrowthReqBuilder;
import com.infinityraider.agricraft.api.v1.requirement.IGrowthRequirement;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.util.BlockRange;
import com.infinityraider.agricraft.api.v1.util.FuzzyStack;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthReqBuilder;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.PlantRenderer;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class JsonPlant implements IAgriPlant {

    private final AgriPlant plant;

    private final List<FuzzyStack> seedItems;
    private final IGrowthRequirement growthRequirement;

    public JsonPlant(AgriPlant plant) {
        this.plant = Objects.requireNonNull(plant, "A JSONPlant may not consist of a null AgriPlant! Why would you even try that!?");
        this.seedItems = initSeedItemsListJSON(plant);
        this.growthRequirement = initGrowthRequirementJSON(plant);
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
    public boolean isFertilizable() {
        return this.plant.canBonemeal();
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
        return this.seedItems;
    }

    @Override
    public final ItemStack getSeed() {
        ItemStack stack = this.getSeedItems().stream()
                .map(s -> s.toStack())
                .findFirst()
                .orElse(new ItemStack(AgriItems.getInstance().AGRI_SEED));
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(AgriNBT.SEED, this.getId());
        new PlantStats().writeToNBT(tag);
        stack.setTagCompound(tag);
        return stack;
    }

    @Override
    public String getInformation() {
        return this.plant.getDescription().toString();
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
    public double getSeedDropChanceBase() {
        return plant.getSeedDropChance();
    }

    @Override
    public double getSeedDropChanceBonus() {
        return plant.getSeedDropBonus();
    }

    @Override
    public int getGrowthStages() {
        return this.plant.getGrowthStages();
    }

    @Override
    public int getTier() {
        return this.plant.getTier();
    }

    @Override
    public void getPossibleProducts(Consumer<ItemStack> products) {
        this.plant.getProducts().getAll().stream()
                .map(p -> p.toStack(FuzzyStack.class))
                .filter(Optional::isPresent)
                .map(p -> p.get().toStack())
                .forEach(products);
    }

    @Override
    public void getHarvestProducts(Consumer<ItemStack> products, IAgriCrop crop, IAgriStat stat, Random rand) {
        this.plant.getProducts().getRandom(rand).stream()
                .map(p -> p.toStack(FuzzyStack.class, rand))
                .filter(Optional::isPresent)
                .map(p -> p.get().toStack())
                .forEach(products);
    }

    @Override
    public IGrowthRequirement getGrowthRequirement() {
        return growthRequirement;
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

    @Override
    @SideOnly(Side.CLIENT)
    public List<BakedQuad> getPlantQuads(IExtendedBlockState state, int growthStage, EnumFacing direction, Function<ResourceLocation, TextureAtlasSprite> textureToIcon) {
        //The quads returned from this method are added to the tessellator,
        // however the plant renderer directly adds them to the tessellator, so an empty list is returned
        if (textureToIcon instanceof ITessellator) {
            PlantRenderer.renderPlant((ITessellator) textureToIcon, this, growthStage);
        }
        return Collections.emptyList();
    }

    public static final List<FuzzyStack> initSeedItemsListJSON(AgriPlant plant) {
        final List<FuzzyStack> seeds = new ArrayList<>(plant.getSeedItems().size());
        plant.getSeedItems().stream()
                .map(i -> i.toStack(FuzzyStack.class))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(seeds::add);
        if (seeds.isEmpty()) {
            seeds.add(new FuzzyStack(new ItemStack(AgriItems.getInstance().AGRI_SEED)));
        }
        return seeds;
    }

    public static final IGrowthRequirement initGrowthRequirementJSON(AgriPlant plant) {

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

}
