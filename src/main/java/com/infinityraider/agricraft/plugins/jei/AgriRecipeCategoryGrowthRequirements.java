package com.infinityraider.agricraft.plugins.jei;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthRequirement;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.infinitylib.render.IRenderUtilities;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class AgriRecipeCategoryGrowthRequirements implements IRecipeCategory<IAgriPlant> {

    public static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "jei/growth_req");

    private final IDrawable icon;
    private final IAgriDrawable background;
    private final IAgriDrawable background_seasons;
    private final Set<TooltipRegion> tooltips;

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(
                AgriApi.getPlantRegistry().stream().filter(IAgriPlant::isPlant).collect(Collectors.toList()),
                ID);
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_wood), AgriRecipeCategoryGrowthRequirements.ID);
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_iron), AgriRecipeCategoryGrowthRequirements.ID);
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_obsidian), AgriRecipeCategoryGrowthRequirements.ID);
    }

    public AgriRecipeCategoryGrowthRequirements(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(new ItemStack(Blocks.FARMLAND));
        this.background = JeiPlugin.createAgriDrawable(
                new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/growth_req.png"),
                0, 0, 128, 128, 128, 128);
        this.background_seasons = JeiPlugin.createAgriDrawable(
                new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/growth_req_seasons.png"),
                0, 0, 128, 128, 128, 128);
        this.tooltips = ImmutableSet.of(
                new TooltipRegion(AgriToolTips.GROWTH, 102, 20, 111, 70),
                new TooltipRegion(AgriApi.getStatRegistry().strengthStat().getDescription(), 114, 20, 123, 70),
                new TooltipRegion(AgriToolTips.LIGHT, 17, 25, 21, 74),
                new TooltipRegion(AgriSeason.SPRING.getDisplayName(), 25, 26, 37, 38, AgriApi.getSeasonLogic()::isActive),
                new TooltipRegion(AgriSeason.SUMMER.getDisplayName(), 25, 38, 37, 50, AgriApi.getSeasonLogic()::isActive),
                new TooltipRegion(AgriSeason.AUTUMN.getDisplayName(), 25, 50, 37, 62, AgriApi.getSeasonLogic()::isActive),
                new TooltipRegion(AgriSeason.WINTER.getDisplayName(), 25, 62, 37, 74, AgriApi.getSeasonLogic()::isActive)
        );
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
        return I18n.format("agricraft.gui.growth_req");
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

    public PlantRenderState getState(IAgriPlant plant) {
        return PlantRenderState.getState(plant);
    }

    @Override
    public void setIngredients(@Nonnull IAgriPlant plant, IIngredients ingredients) {
        // Get current strength and stage
        PlantRenderState state = this.getState(plant);
        int strength = state.getStrength();
        IAgriGrowthStage stage = state.getStage();
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
        PlantRenderState state = this.getState(plant);
        int strength = state.getStrength();
        IAgriGrowthStage stage = state.getStage();
        IAgriGrowthRequirement req = plant.getGrowthRequirement(stage);
        // Tell the renderer to render with a custom growth stage
        AgriIngredientPlant.RENDERER.useGrowthStageForNextRenderCall(plant, stage);
        // Draw increments
        IncrementRenderer.getInstance().renderStrengthIncrements(transforms, strength);
        IncrementRenderer.getInstance().renderGrowthStageIncrements(transforms, stage);
        // Draw light levels
        LightLevelRenderer.getInstance().renderLightLevels(transforms, 18, 26, light -> req.isLightLevelAccepted(light, strength));
        // Draw Property icons
        Arrays.stream(IAgriSoil.Humidity.values()).filter(IAgriSoil.Humidity::isValid)
                .filter(humidity -> req.isSoilHumidityAccepted(humidity, strength))
                .forEach(humidity -> SoilPropertyIconRenderer.getInstance().drawIcon(humidity, transforms, 37, 83, mouseX, mouseY));
        Arrays.stream(IAgriSoil.Acidity.values()).filter(IAgriSoil.Acidity::isValid)
                .filter(acidity -> req.isSoilAcidityAccepted(acidity, strength))
                .forEach(acidity -> SoilPropertyIconRenderer.getInstance().drawIcon(acidity, transforms, 37, 96, mouseX, mouseY));
        Arrays.stream(IAgriSoil.Nutrients.values()).filter(IAgriSoil.Nutrients::isValid)
                .filter(nutrients ->req.isSoilNutrientsAccepted(nutrients, strength))
                .forEach(nutrients -> SoilPropertyIconRenderer.getInstance().drawIcon(nutrients, transforms, 37, 109, mouseX, mouseY));
        // Draw seasons
        SeasonRenderer.getInstance().renderSeasons(transforms, 25, 26, season -> req.isSeasonAccepted(season, strength));
        // Draw buttons
        state.updateStageButtons(102, 10);
        state.updateStrengthButtons(114, 10 );
        state.drawGrowthStageButtons(transforms, mouseX, mouseY);
        state.drawStrengthButtons(transforms, mouseX, mouseY);
        // Draw tooltips
        this.tooltips.forEach(tooltip -> tooltip.drawTooltip(transforms, mouseX, mouseY));
    }

    @Override
    public boolean handleClick(@Nonnull IAgriPlant plant, double mouseX, double mouseY, int mouseButton) {
        // Fetch strength and stage
        if(mouseButton == 0) {
            PlantRenderState state = this.getState(plant);
            state.updateStageButtons(102, 10);
            state.updateStrengthButtons(114, 10 );
            return state.onClick(mouseX, mouseY);
        } else {
            return IRecipeCategory.super.handleClick(plant, mouseX, mouseY, mouseButton);
        }
    }

    public static class PlantRenderState {
        private static final Map<IAgriPlant, PlantRenderState> instances = Maps.newIdentityHashMap();

        public static PlantRenderState getState(IAgriPlant plant) {
            return instances.computeIfAbsent(plant, PlantRenderState::new);
        }

        private final IAgriPlant plant;
        private final List<IAgriGrowthStage> stages;

        private int stage;
        private int strength;

        private final Button incStrButton;
        private final Button decStrButton;

        private final Button incStageButton;
        private final Button decStageButton;

        private final Set<Button> buttons;

        private PlantRenderState(IAgriPlant plant) {
            this.plant = plant;
            this.stages = plant.getGrowthStages().stream()
                    .sorted((s1, s2) -> (int) (100*(s1.growthPercentage() - s2.growthPercentage())))
                    .collect(Collectors.toList());
            this.stage = 0;
            this.strength = this.getMinStrength();
            ResourceLocation incTexture = new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/inc_button.png");
            ResourceLocation decTexture = new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/dec_button.png");
            this.incStrButton = new Button(incTexture, this::incrementStrength);
            this.decStrButton = new Button(decTexture, this::decrementStrength);
            this.incStageButton = new Button(incTexture, this::incrementStage);
            this.decStageButton = new Button(decTexture, this::decrementStage);
            this.buttons = ImmutableSet.of(this.incStrButton, this.decStrButton, this.incStageButton, this.decStageButton);
        }

        public IAgriPlant getPlant() {
            return this.plant;
        }

        public IAgriGrowthStage getStage() {
            return this.stages.get(this.stage);
        }

        public boolean incrementStage() {
            this.stage = Math.min(this.stages.size() - 1, this.stage + 1);
            return true;
        }

        public boolean decrementStage() {
            this.stage = Math.max(0, this.stage - 1);
            return true;
        }

        public int getStrength() {
            return this.strength;
        }

        public int getMinStrength() {
            return AgriApi.getStatRegistry().strengthStat().getMin();
        }

        public int getMaxStrength() {
            return AgriApi.getStatRegistry().strengthStat().getMax();
        }

        public boolean incrementStrength() {
            this.strength = Math.min(this.getMaxStrength(), this.getStrength() + 1);
            return true;
        }

        public boolean decrementStrength() {
            this.strength = Math.max(this.getMinStrength(), this.getStrength() - 1);
            return true;
        }

        public void updateStrengthButtons(int x, int y) {
            this.incStrButton.updatePosition(x, y);
            this.decStrButton.updatePosition(x, y + 61);
        }

        public void updateStageButtons(int x, int y) {
            this.incStageButton.updatePosition(x, y);
            this.decStageButton.updatePosition(x, y + 61);
        }

        public void drawStrengthButtons(MatrixStack transforms, double mX, double mY) {
            this.incStrButton.draw(transforms, mX, mY);
            this.decStrButton.draw(transforms, mX, mY);
        }

        public void drawGrowthStageButtons(MatrixStack transforms, double mX, double mY) {
            this.incStageButton.draw(transforms, mX, mY);
            this.decStageButton.draw(transforms, mX, mY);
        }

        public boolean onClick(double mX, double mY) {
            return this.buttons.stream()
                    .filter(button -> button.isOver(mX, mY))
                    .findFirst()
                    .map(Button::onPress)
                    .orElse(false);
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

        public void draw(MatrixStack transforms, double mX, double mY) {
            int u = 0;
            int v = 0;
            if(this.isOver(mX, mY)) {
                if(Minecraft.getInstance().mouseHelper.isLeftDown()) {
                    u = 9;
                } else {
                    u = 18;
                }
            }
            this.bindTexture(this.texture);
            AbstractGui.blit(transforms, this.x, this.y, 9, 9, u, v, 9, 9, 27, 9);
        }

        public boolean onPress() {
            return this.onPress.getAsBoolean();
        }
    }

    private static final class TooltipRegion implements IRenderUtilities {
        private final List<ITextComponent> tooltip;
        private final int x1;
        private final int y1;
        private final int x2;
        private final int y2;
        private final BooleanSupplier isActive;

        public TooltipRegion(ITextComponent tooltip, int x1, int y1, int x2, int y2) {
            this(ImmutableList.of(tooltip), x1, y1, x2, y2);
        }

        public TooltipRegion(ITextComponent tooltip, int x1, int y1, int x2, int y2, BooleanSupplier isActive) {
            this(ImmutableList.of(tooltip), x1, y1, x2, y2, isActive);
        }

        public TooltipRegion(List<ITextComponent> tooltip, int x1, int y1, int x2, int y2) {
            this(tooltip, x1, y1, x2, y2, () -> true);
        }

        public TooltipRegion(List<ITextComponent> tooltip, int x1, int y1, int x2, int y2, BooleanSupplier isActive) {
            this.tooltip = tooltip;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.isActive = isActive;
        }

        public void drawTooltip(MatrixStack transforms, double mX, double mY) {
            if(this.isActive.getAsBoolean()) {
                if (mX >= this.x1 && mX <= this.x2 && mY >= this.y1 && mY <= this.y2) {
                    int w = this.getScaledWindowWidth();
                    int h = this.getScaledWindowHeight();
                    GuiUtils.drawHoveringText(transforms, tooltip, (int) mX, (int) mY, w, h, -1, this.getFontRenderer());
                }
            }
        }
    }

    private static final class LightLevelRenderer implements IRenderUtilities {
        private static final LightLevelRenderer INSTANCE = new LightLevelRenderer();

        public static LightLevelRenderer getInstance() {
            return INSTANCE;
        }

        private final ResourceLocation texture = new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/light_levels.png");

        private LightLevelRenderer() {}

        public void renderLightLevels(MatrixStack transforms, int x, int y, Predicate<Integer> predicate) {
            this.bindTexture(this.texture);
            for(int i = 15; i >= 0; i--) {
                if(predicate.test(i))
                AbstractGui.blit(transforms, x, y + 3*(15-i), 3, 3, 0, 3*(15-i), 3, 3, 3, 48);
            }
        }
    }

    private static final class SeasonRenderer implements IRenderUtilities {
        private static final SeasonRenderer INSTANCE = new SeasonRenderer();

        public static SeasonRenderer getInstance() {
            return INSTANCE;
        }

        private final ResourceLocation texture = new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/season_icons.png");

        private SeasonRenderer() {
        }

        public void renderSeasons(MatrixStack transforms, int x, int y, Predicate<AgriSeason> predicate) {
            if (AgriApi.getSeasonLogic().isActive()) {
                this.bindTexture(this.texture);
                AgriSeason.stream().filter(predicate).forEach(season -> {
                    int i = season.ordinal();
                    AbstractGui.blit(transforms, x, y + 12 * i, 12, 12, 0, 12 * i, 12, 12, 12, 48);
                });
            }
        }
    }

    private static final class IncrementRenderer implements IRenderUtilities {
        private static final IncrementRenderer INSTANCE = new IncrementRenderer();

        public static IncrementRenderer getInstance() {
            return INSTANCE;
        }

        private final ResourceLocation texture = new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/growth_req_increments.png");

        private IncrementRenderer() {}

        public void renderStrengthIncrements(MatrixStack transforms, int strength) {
            this.bindTexture(this.texture);
            for(int i = 0; i < strength; i++) {
                AbstractGui.blit(transforms, 115, 66 - i*5, 7, 3, 0, 0, 7, 3, 7, 4);
            }
        }

        public void renderGrowthStageIncrements(MatrixStack transforms, IAgriGrowthStage stage) {
            this.bindTexture(this.texture);
            double f = stage.growthPercentage();
            int l = 48;
            int h = (int) (l*f);
            int y = 21 + l - h;
            AbstractGui.blit(transforms, 103, y, 7, h, 0, 3, 7, 1, 7, 4);
        }
    }
}
