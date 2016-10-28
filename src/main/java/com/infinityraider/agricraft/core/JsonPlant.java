/*
 * Vanilla Crop Class.
 */
package com.infinityraider.agricraft.core;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.api.render.RenderMethod;
import com.infinityraider.agricraft.api.requirement.IGrowthReqBuilder;
import com.infinityraider.agricraft.api.requirement.IGrowthRequirement;
import com.infinityraider.agricraft.api.util.FuzzyStack;
import com.infinityraider.agricraft.farming.CropPlant;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.utility.IconHelper;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriPlant;
import com.agricraft.agricore.util.TypeHelper;

public class JsonPlant extends CropPlant {

    public final AgriPlant plant;

    private List<Item> seedItems;

    public JsonPlant(AgriPlant plant) {
        this.plant = plant;
        this.setGrowthRequirement(this.initGrowthRequirementJSON());
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
    public Collection<Item> getSeedItems() {
        if (this.seedItems == null) {
            this.seedItems = this.plant.getSeedItems().stream()
                    .map(i -> Item.getByNameOrId(i))
                    .filter(TypeHelper::isNonNull)
                    .collect(Collectors.toList());
        }
        return this.seedItems;
    }

    @Override
    public String getInformation() {
        return this.plant.getDescription().toString();
    }

    @Override
    public boolean isWeedable() {
        return this.plant.isWeedable();
    }

    @Override
    public boolean isAgressive() {
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
    public List<ItemStack> getAllFruits() {
        return this.plant.getProducts().getAll().stream()
                .map(p -> p.toStack())
                .filter(p -> p instanceof FuzzyStack)
                .map(p -> ((FuzzyStack) p).toStack())
                .collect(Collectors.toList());
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return this.plant.getProducts().getRandom(rand).stream()
                .map(p -> p.toStack())
                .filter(p -> p instanceof FuzzyStack)
                .map(p -> ((FuzzyStack) p).toStack())
                .findFirst()
                .orElse(null);
    }

    @Override
    public void onAllowedGrowthTick(World world, BlockPos pos, int oldGrowthStage) {
        // Holder
    }

    @Override
    protected IGrowthRequirement initGrowthRequirement() {
        // Hack to avert annoying auto-call.
        return GrowthRequirementHandler.getNewBuilder().build();
    }

    protected final IGrowthRequirement initGrowthRequirementJSON() {

        IGrowthReqBuilder builder = GrowthRequirementHandler.getNewBuilder();

        if (this.plant == null) {
            AgriCore.getLogger("AgriCraft").warn("Null plant!");
            return builder.build();
        }

        this.plant.getRequirement().getSoils().stream()
                .map(JsonSoil::new)
                .forEach(builder::addSoil);

        this.plant.getRequirement().getNearby().forEach((obj, dist) -> {
            if (obj instanceof FuzzyStack) {
                FuzzyStack stack = (FuzzyStack) obj;
                builder.addRequiredBlock(stack, dist);
            }
        });

        builder.setMinLight(this.plant.getRequirement().getMinLight());
        builder.setMaxLight(this.plant.getRequirement().getMaxLight());

        return builder.build();

    }

    @Override
    public boolean canBonemeal() {
        return this.plant.canBonemeal();
    }

    @Override
    public Block getBlock() {
        return null;
    }

    @Override
    public int getTier() {
        return this.plant == null ? 1 : this.plant.getTier();
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

    @SideOnly(Side.CLIENT)
    public void registerIcons() {
        for (String tex : this.plant.getTexture().getPlantTextures()) {
            //AgriCore.getLogger("AgriCraft").debug("Registering: " + tex);
            IconHelper.registerIcon(tex);
        }
    }

}
