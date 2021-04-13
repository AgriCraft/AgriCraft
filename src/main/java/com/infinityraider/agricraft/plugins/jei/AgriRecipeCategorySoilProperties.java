package com.infinityraider.agricraft.plugins.jei;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowable;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthRequirement;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class AgriRecipeCategorySoilProperties implements IRecipeCategory<IAgriPlant> {

    public static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "jei/soil");

    private final Map<IAgriPlant, Integer> strengthMap;
    private final Map<IAgriPlant, IAgriGrowthStage> stageMap;

    public final IDrawable icon;
    public final IAgriDrawable background;

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(AgriApi.getPlantRegistry().all(), ID);
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_wood), AgriRecipeCategorySoilProperties.ID);
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_iron), AgriRecipeCategorySoilProperties.ID);
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_obsidian), AgriRecipeCategorySoilProperties.ID);
    }

    public AgriRecipeCategorySoilProperties(IGuiHelper helper) {
        this.strengthMap = Maps.newIdentityHashMap();
        this.stageMap = Maps.newIdentityHashMap();
        this.icon = helper.createDrawableIngredient(new ItemStack(Blocks.FARMLAND));
        this.background = JeiPlugin.createAgriDrawable(new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/soil_properties.png"), 0, 0, 128, 128, 128, 128);
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Nonnull
    @Override
    public Class<IAgriPlant> getRecipeClass() {
        return IAgriPlant.class;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.format("agricraft.gui.soil");
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    protected int getStrength(IAgriPlant plant) {
        return this.strengthMap.computeIfAbsent(plant, aPlant -> AgriApi.getStatRegistry().strengthStat().getMin());
    }

    protected IAgriGrowthStage getStage(IAgriPlant plant) {
        return this.stageMap.computeIfAbsent(plant, IAgriGrowable::getInitialGrowthStage);
    }

    @Override
    public void setIngredients(@Nonnull IAgriPlant plant, IIngredients ingredients) {
        // Get current strength and stage
        int strength = this.getStrength(plant);
        IAgriGrowthStage stage = this.getStage(plant);
        // Determine inputs
        List<ItemStack> seeds = ImmutableList.of(plant.toItemStack());
        List<ItemStack> soils = AgriApi.getSoilRegistry().stream()
                .filter(soil -> {
                    IAgriGrowthRequirement req = plant.getGrowthRequirement(stage);
                    boolean humidity = req.isSoilHumidityAccepted(soil.getHumidity(), strength);
                    boolean acidity = req.isSoilAcidityAccepted(soil.getAcidity(), strength);
                    boolean nutrients = req.isSoilNutrientsAccepted(soil.getNutrients(), strength);
                    return humidity && acidity && nutrients;})
                .map(IAgriSoil::getVariants)
                .flatMap(Collection::stream)
                .map(AbstractBlock.AbstractBlockState::getBlock)
                .distinct()
                .map(ItemStack::new)
                .collect(Collectors.toList());
        ingredients.setInputLists(VanillaTypes.ITEM, ImmutableList.of(seeds, soils));
        // Determine output
        ingredients.setOutputLists(AgriIngredientPlant.TYPE, ImmutableList.of(ImmutableList.of(plant)));
    }

    @Override
    public void setRecipe(IRecipeLayout layout, @Nonnull IAgriPlant plant, @Nonnull IIngredients ingredients) {
        // Clear the focus as this sometimes causes display bugs
        layout.getIngredientsGroup(AgriIngredientPlant.TYPE).setOverrideDisplayFocus(null);
        layout.getIngredientsGroup(VanillaTypes.ITEM).setOverrideDisplayFocus(null);

        // Denote that this is a shapeless recipe.
        layout.setShapeless();

        // Setup inputs (seed and soil)
        layout.getIngredientsGroup(VanillaTypes.ITEM).init(0, true, 55, 2);
        layout.getIngredientsGroup(VanillaTypes.ITEM).init(1, true, 55, 62);

        // Setup output (plant)
        layout.getIngredientsGroup(AgriIngredientPlant.TYPE).init(0, false, 56, 43);

        // Register Recipe Elements
        layout.getItemStacks().set(ingredients);
        layout.getIngredientsGroup(AgriIngredientPlant.TYPE).set(ingredients);
    }

    @Override
    public void draw(@Nonnull IAgriPlant plant, @Nonnull MatrixStack transforms, double mouseX, double mouseY) {
        // Fetch strength and stage
        int strength = this.getStrength(plant);
        IAgriGrowthStage stage = this.getStage(plant);
        // Draw Property icons
        Arrays.stream(IAgriSoil.Humidity.values()).filter(IAgriSoil.Humidity::isValid)
                .filter(humidity -> plant.getGrowthRequirement(stage).isSoilHumidityAccepted(humidity, strength))
                .forEach(humidity -> SoilPropertyIconRenderer.getInstance().drawIcon(humidity, transforms, 37, 83, mouseX, mouseY));
        Arrays.stream(IAgriSoil.Acidity.values()).filter(IAgriSoil.Acidity::isValid)
                .filter(acidity -> plant.getGrowthRequirement(stage).isSoilAcidityAccepted(acidity, strength))
                .forEach(acidity -> SoilPropertyIconRenderer.getInstance().drawIcon(acidity, transforms, 37, 96, mouseX, mouseY));
        Arrays.stream(IAgriSoil.Nutrients.values()).filter(IAgriSoil.Nutrients::isValid)
                .filter(nutrients -> plant.getGrowthRequirement(stage).isSoilNutrientsAccepted(nutrients, strength))
                .forEach(nutrients -> SoilPropertyIconRenderer.getInstance().drawIcon(nutrients, transforms, 37, 109, mouseX, mouseY));
    }
}
