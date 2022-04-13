package com.infinityraider.agricraft.plugins.jei;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthRequirement;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.render.plant.gui.LightLevelRenderer;
import com.infinityraider.agricraft.render.plant.gui.SeasonRenderer;
import com.infinityraider.agricraft.render.plant.gui.SoilPropertyIconRenderer;
import com.infinityraider.infinitylib.utility.TooltipRegion;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AgriRecipeCategoryGrowthRequirements implements IRecipeCategory<AgriRecipeCategoryGrowthRequirements.Recipe> {

    public static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "jei/growth_req");
    public static final RecipeType<Recipe> TYPE = new RecipeType<>(ID, Recipe.class);

    private static final TranslatableComponent TITLE = new TranslatableComponent("agricraft.gui.growth_req");

    private final IDrawable icon;
    private final IAgriDrawable background;
    private final IAgriDrawable background_seasons;

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(
                TYPE,
                AgriApi.getPlantRegistry().stream()
                        .filter(IAgriPlant::isPlant)
                        .map(Recipe::new)
                        .collect(Collectors.toList()));
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(AgriApi.getAgriContent().getItems().getWoodCropSticksItem()), TYPE);
        registration.addRecipeCatalyst(new ItemStack(AgriApi.getAgriContent().getItems().getIronCropSticksItem()), TYPE);
        registration.addRecipeCatalyst(new ItemStack(AgriApi.getAgriContent().getItems().getObsidianCropSticksItem()), TYPE);
    }

    public AgriRecipeCategoryGrowthRequirements(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(Blocks.FARMLAND));
        this.background = JeiPlugin.createAgriDrawable(
                new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/growth_req.png"),
                0, 0, 128, 128, 128, 128);
        this.background_seasons = JeiPlugin.createAgriDrawable(
                new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/growth_req_seasons.png"),
                0, 0, 128, 128, 128, 128);
    }

    @Override
    public RecipeType<Recipe> getRecipeType() {
        return TYPE;
    }

    @Nonnull
    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public ResourceLocation getUid() {
        return ID;
    }

    @Nonnull
    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public Class<Recipe> getRecipeClass() {
        return Recipe.class;
    }

    @Nonnull
    @Override
    public TranslatableComponent getTitle() {
        return TITLE;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return AgriApi.getSeasonLogic().isActive() ? this.background_seasons : this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    private static final String INPUT_SEED = "input_seed";
    private static final String INPUT_SOIL = "input_soil";
    private static final String OUTPUT_PLANT = "output_plant";

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Recipe recipe, IFocusGroup focuses) {
        // Clear the focus as this sometimes causes display bugs
        //layout.getIngredientsGroup(AgriIngredientPlant.TYPE).setOverrideDisplayFocus(null);
        //layout.getIngredientsGroup(VanillaTypes.ITEM).setOverrideDisplayFocus(null);

        // Set shapeless
        builder.setShapeless();

        // Seed
        builder.addSlot(RecipeIngredientRole.INPUT, 55, 2)
                .setSlotName(INPUT_SEED)
                .addIngredient(VanillaTypes.ITEM, recipe.getPlant().toItemStack());

        // Plant
        builder.addSlot(RecipeIngredientRole.OUTPUT, 56, 43)
                .setSlotName(OUTPUT_PLANT)
                .setCustomRenderer(AgriIngredientPlant.TYPE, AgriIngredientPlant.RENDERER)
                .addIngredient(AgriIngredientPlant.TYPE, recipe.getPlant());

        // Soils
        builder.addSlot(RecipeIngredientRole.INPUT, 55, 62)
                .setSlotName(INPUT_SOIL)
                .addIngredients(VanillaTypes.ITEM,
                        AgriApi.getSoilRegistry().stream()
                                .filter(soil -> {
                                    IAgriGrowthRequirement req = recipe.getPlant().getGrowthRequirement(recipe.getCurrentStage());
                                    boolean humidity = req.getSoilHumidityResponse(soil.getHumidity(), recipe.getCurrentStrength()).isFertile();
                                    boolean acidity = req.getSoilAcidityResponse(soil.getAcidity(), recipe.getCurrentStrength()).isFertile();
                                    boolean nutrients = req.getSoilNutrientsResponse(soil.getNutrients(), recipe.getCurrentStrength()).isFertile();
                                    return humidity && acidity && nutrients;
                                })
                                .map(IAgriSoil::getVariants)
                                .flatMap(Collection::stream)
                                .map(BlockBehaviour.BlockStateBase::getBlock)
                                .distinct()
                                .map(ItemStack::new)
                                .collect(Collectors.toList()));

        // Register Recipe Elements
        //layout.getItemStacks().set(ingredients);
        //layout.getIngredientsGroup(AgriIngredientPlant.TYPE).set(ingredients);
    }

    @Override
    public void draw(Recipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack transforms, double mouseX, double mouseY) {
        // Fetch strength and stage
        int strength = recipe.getCurrentStrength();
        IAgriGrowthStage stage = recipe.getCurrentStage();
        IAgriGrowthRequirement req = recipe.getGrowthRequirement();
        // Tell the renderer to render with a custom growth stage
        AgriIngredientPlant.RENDERER.useGrowthStageForNextRenderCall(recipe.getPlant(), stage);
        // Draw increments
        IncrementRenderer.getInstance().renderStrengthIncrements(transforms, strength);
        IncrementRenderer.getInstance().renderGrowthStageIncrements(transforms, stage);
        // Draw light levels
        LightLevelRenderer.getInstance().renderLightLevels(transforms, 32, 26, mouseX, mouseY, light -> req.getLightLevelResponse(light, strength).isFertile());
        // Draw Property icons
        Arrays.stream(IAgriSoil.Humidity.values()).filter(IAgriSoil.Humidity::isValid)
                .filter(humidity -> req.getSoilHumidityResponse(humidity, strength).isFertile())
                .forEach(humidity -> SoilPropertyIconRenderer.getInstance().drawHumidityIcon(humidity, transforms, 37, 83, mouseX, mouseY));
        Arrays.stream(IAgriSoil.Acidity.values()).filter(IAgriSoil.Acidity::isValid)
                .filter(acidity -> req.getSoilAcidityResponse(acidity, strength).isFertile())
                .forEach(acidity -> SoilPropertyIconRenderer.getInstance().drawAcidityIcon(acidity, transforms, 37, 96, mouseX, mouseY));
        Arrays.stream(IAgriSoil.Nutrients.values()).filter(IAgriSoil.Nutrients::isValid)
                .filter(nutrients ->req.getSoilNutrientsResponse(nutrients, strength).isFertile())
                .forEach(nutrients -> SoilPropertyIconRenderer.getInstance().drawNutrientsIcon(nutrients, transforms, 37, 109, mouseX, mouseY));
        // Draw seasons
        SeasonRenderer.getInstance().renderSeasons(transforms, 17, 24, season -> req.getSeasonResponse(season, strength).isFertile());
        // Draw buttons
        recipe.updateStageButtons(102, 10);
        recipe.updateStrengthButtons(114, 10 );
        recipe.drawGrowthStageButtons(transforms, mouseX, mouseY);
        recipe.drawStrengthButtons(transforms, mouseX, mouseY);
    }

    @Override
    public List<Component> getTooltipStrings(Recipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        return recipe.getTooltips(mouseX, mouseY);
    }

    @Override
    public boolean handleInput(@Nonnull Recipe recipe, double mouseX, double mouseY, InputConstants.Key input) {
        if (input.getType() == InputConstants.Type.MOUSE) {
            int mouseButton = input.getValue();
            // Fetch strength and stage
            if (mouseButton == 0) {
                recipe.updateStageButtons(102, 10);
                recipe.updateStrengthButtons(114, 10);
                return recipe.onClick(mouseX, mouseY);
            }
        }
        return IRecipeCategory.super.handleInput(recipe, mouseX, mouseY, input);
    }

    public static class Recipe {
        private final IAgriPlant plant;
        private final List<IAgriGrowthStage> stages;

        private int currentStage;
        private int currentStrength;

        private final Button incStrButton;
        private final Button decStrButton;

        private final Button incStageButton;
        private final Button decStageButton;

        private final Set<Button> buttons;
        private final Set<TooltipRegion<Recipe>> tooltips;

        private Recipe(IAgriPlant plant) {
            // set the plant
            this.plant = plant;
            // cache growth stages
            this.stages = plant.getGrowthStages().stream()
                    .sorted((s1, s2) -> (int) (100 * (s1.growthPercentage() - s2.growthPercentage())))
                    .collect(Collectors.toList());
            // initialize default current values
            this.currentStrength = this.getMinStrength();
            this.currentStage = 0;
            // initialize buttons
            ResourceLocation incTexture = new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/inc_button.png");
            ResourceLocation decTexture = new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/dec_button.png");
            this.incStrButton = new Button(incTexture, this::incrementStrength);
            this.decStrButton = new Button(decTexture, this::decrementStrength);
            this.incStageButton = new Button(incTexture, this::incrementStage);
            this.decStageButton = new Button(decTexture, this::decrementStage);
            this.buttons = ImmutableSet.of(this.incStrButton, this.decStrButton, this.incStageButton, this.decStageButton);
            // initialize tooltips
            this.tooltips = this.initTooltips();
        }

        public IAgriPlant getPlant() {
            return this.plant;
        }

        public IAgriGrowthRequirement getGrowthRequirement() {
            return this.getPlant().getGrowthRequirement(this.getCurrentStage());
        }

        public IAgriGrowthStage getCurrentStage() {
            return this.stages.get(this.currentStage);
        }

        public int getCurrentStrength() {
            return this.currentStrength;
        }

        public boolean incrementStage() {
            this.currentStage = Math.min(this.stages.size() - 1, this.currentStage + 1);
            return true;
        }

        public boolean decrementStage() {
            this.currentStage = Math.max(0, this.currentStage - 1);
            return true;
        }

        public int getMinStrength() {
            return AgriApi.getStatRegistry().strengthStat().getMin();
        }

        public int getMaxStrength() {
            return AgriApi.getStatRegistry().strengthStat().getMax();
        }

        public boolean incrementStrength() {
            this.currentStrength = Math.min(this.getMaxStrength(), this.getCurrentStrength() + 1);
            return true;
        }

        public boolean decrementStrength() {
            this.currentStrength = Math.max(this.getMinStrength(), this.getCurrentStrength() - 1);
            return true;
        }

        public boolean onClick(double mX, double mY) {
            return this.buttons.stream()
                    .filter(button -> button.isOver(mX, mY))
                    .findFirst()
                    .map(Button::onPress)
                    .orElse(false);
        }

        public void updateStrengthButtons(int x, int y) {
            this.incStrButton.updatePosition(x, y);
            this.decStrButton.updatePosition(x, y + 61);
        }

        public void updateStageButtons(int x, int y) {
            this.incStageButton.updatePosition(x, y);
            this.decStageButton.updatePosition(x, y + 61);
        }

        public void drawStrengthButtons(PoseStack transforms, double mX, double mY) {
            this.incStrButton.draw(transforms, mX, mY);
            this.decStrButton.draw(transforms, mX, mY);
        }

        public void drawGrowthStageButtons(PoseStack transforms, double mX, double mY) {
            this.incStageButton.draw(transforms, mX, mY);
            this.decStageButton.draw(transforms, mX, mY);
        }

        public List<Component> getTooltips(double mx, double my) {
            return this.tooltips.stream()
                    .filter(tooltip -> tooltip.isActive(mx, my))
                    .findFirst()
                    .map(tooltip -> tooltip.getTooltips(this))
                    .orElse(Collections.emptyList());
        }

        private Set<TooltipRegion<Recipe>> initTooltips() {
            // initialize builder
            ImmutableSet.Builder<TooltipRegion<Recipe>> builder = ImmutableSet.builder();
            // growth tooltip
            Function<Recipe, List<Component>> growth = (state) -> ImmutableList.of(AgriToolTips.getGrowthTooltip(this.getCurrentStage()));
            builder.add(new TooltipRegion<>(growth, 102, 20, 111, 70));
            // strength tooltip
            Function<Recipe, List<Component>> strength = (state) -> ImmutableList.of(new TextComponent("")
                    .append(AgriApi.getStatRegistry().strengthStat().getDescription())
                    .append(new TextComponent(": " + this.getCurrentStrength())));
            builder.add(new TooltipRegion<>(strength, 114, 20, 123, 70));
            // soil property tooltips
            SoilPropertyIconRenderer.getInstance().defineHumidityTooltips(tooltip -> addToBuilder(builder, tooltip), 37, 83);
            SoilPropertyIconRenderer.getInstance().defineAcidityTooltips(tooltip -> addToBuilder(builder, tooltip), 37, 96);
            SoilPropertyIconRenderer.getInstance().defineNutrientsTooltips(tooltip -> addToBuilder(builder, tooltip), 37, 109);
            // season tooltips
            builder.add(new TooltipRegion<>(AgriSeason.SPRING.getDisplayName(), 17, 24, 29, 36, AgriApi.getSeasonLogic()::isActive));
            builder.add(new TooltipRegion<>(AgriSeason.SUMMER.getDisplayName(), 17, 37, 29, 49, AgriApi.getSeasonLogic()::isActive));
            builder.add(new TooltipRegion<>(AgriSeason.AUTUMN.getDisplayName(), 17, 50, 29, 62, AgriApi.getSeasonLogic()::isActive));
            builder.add(new TooltipRegion<>(AgriSeason.WINTER.getDisplayName(), 17, 63, 29, 74, AgriApi.getSeasonLogic()::isActive));
            // light level tooltips
            LightLevelRenderer.getInstance().defineTooltips(tooltip -> addToBuilder(builder, tooltip), 32, 26);
            // return tooltips
            return builder.build();
        }

        // Generics are the nail in my coffin...
        @SuppressWarnings("unchecked")
        private void addToBuilder(ImmutableSet.Builder<TooltipRegion<Recipe>> builder, TooltipRegion<?> tooltip) {
            builder.add((TooltipRegion<Recipe>) tooltip);
        }
    }

    private static final class Button implements IRenderUtilities {
        private final ResourceLocation texture;
        private final BooleanSupplier onPress;

        private int x;
        private int y;

        public Button(ResourceLocation texture, BooleanSupplier onPress) {
            this.texture = texture;
            this.onPress = onPress;
        }

        public void updatePosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean isOver(double x, double y) {
            return x >= this.x && x <= this.x + 9 && y >= this.y && y <= this.y + 9;
        }

        public void draw(PoseStack transforms, double mX, double mY) {
            int u = 0;
            int v = 0;
            if(this.isOver(mX, mY)) {
                if(Minecraft.getInstance().mouseHandler.isLeftPressed()) {
                    u = 9;
                } else {
                    u = 18;
                }
            }
            this.bindTexture(this.texture);
            Screen.blit(transforms, this.x, this.y, 9, 9, u, v, 9, 9, 27, 9);
        }

        public boolean onPress() {
            return this.onPress.getAsBoolean();
        }
    }

    private static final class IncrementRenderer implements IRenderUtilities {
        private static final IncrementRenderer INSTANCE = new IncrementRenderer();

        public static IncrementRenderer getInstance() {
            return INSTANCE;
        }

        private final ResourceLocation texture = new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/growth_req_increments.png");

        private IncrementRenderer() {}

        public void renderStrengthIncrements(PoseStack transforms, int strength) {
            this.bindTexture(this.texture);
            for(int i = 0; i < strength; i++) {
                Screen.blit(transforms, 115, 66 - i*5, 7, 3, 0, 0, 7, 3, 7, 4);
            }
        }

        public void renderGrowthStageIncrements(PoseStack transforms, IAgriGrowthStage stage) {
            this.bindTexture(this.texture);
            double f = stage.growthPercentage();
            int l = 48;
            int h = (int) (l*f);
            int y = 21 + l - h;
            Screen.blit(transforms, 103, y, 7, h, 0, 3, 7, 1, 7, 4);
        }
    }
}
