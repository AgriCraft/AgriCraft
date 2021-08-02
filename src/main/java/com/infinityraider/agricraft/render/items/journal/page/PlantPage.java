package com.infinityraider.agricraft.render.items.journal.page;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthRequirement;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.capability.CapabilityResearchedPlants;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.network.MessagePlantResearched;
import com.infinityraider.agricraft.render.items.journal.PageRenderer;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PlantPage extends BasePage implements IRenderUtilities {
    private static final ITextComponent GROWTH_STAGES = new TranslationTextComponent("agricraft.tooltip.growth_stages");
    private static final ITextComponent GROWTH_REQUIREMENTS = new TranslationTextComponent("agricraft.tooltip.growth_requirements");
    private static final ITextComponent PRODUCTS = new TranslationTextComponent("agricraft.tooltip.products");
    private static final ITextComponent MUTATIONS = new TranslationTextComponent("agricraft.tooltip.mutations");

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

    @Override
    public void onPageOpened() {
        if(!CapabilityResearchedPlants.getInstance().isPlantResearched(AgriCraft.instance.getClientPlayer(), this.plant)) {
            new MessagePlantResearched(this.plant).sendToServer();
        }
    }

    public List<List<IAgriPlant>> getOffPageMutations() {
        return this.mutationsOffPage;
    }

    @Override
    public void drawLeftSheet(PageRenderer renderer, MatrixStack transforms) {
        // Title
        renderer.drawTexture(transforms, Textures.TITLE, 0, 2, 128, 20);
        renderer.drawText(transforms, this.plant.getSeedName(), 30, 10);
        // Description
        float offset = renderer.drawText(transforms, this.plant.getInformation(), 10, 30, 0.70F);
        // Growth requirements
        this.drawGrowthRequirements(renderer, transforms,35 + offset);
        // Seed
        this.plant.getGuiRenderer().drawSeed(this.plant, renderer, transforms, 4, 5, 16, 16);
        // Growth stages
        this.drawGrowthStages(renderer, transforms);
    }

    @Override
    public void drawRightSheet(PageRenderer renderer, MatrixStack transforms) {
        // Products
        this.drawProducts(renderer, transforms);
        // Mutations
        this.drawMutations(renderer, transforms);
    }

    protected void drawGrowthRequirements(PageRenderer renderer, MatrixStack transforms, float offset) {
        float dy = Math.max(offset, 60);
        dy += renderer.drawText(transforms, GROWTH_REQUIREMENTS, 10, dy, 0.80F) + 1;
        // Light level
        renderer.drawTexture(transforms, Textures.BRIGHTNESS_BAR, 6, dy, 66, 8);
        transforms.push();
        transforms.translate(0, 0, -0.001F);
        for (int i = 0; i < this.brightnessMask.length; i++) {
            boolean current = this.brightnessMask[i];
            if(current) {
                boolean prev = i > 0 && this.brightnessMask[i - 1];
                boolean next = i < (this.brightnessMask.length - 1) && this.brightnessMask[i + 1];
                renderer.drawTexture(transforms, Textures.BRIGHTNESS_HIGHLIGHT, 6 + 4*i + 1, dy, 4, 8,
                        0.25F, 0, 0.75F, 1);
                if(!prev) {
                    renderer.drawTexture(transforms, Textures.BRIGHTNESS_HIGHLIGHT, 6 + 4 * i, dy, 1, 8,
                            0, 0, 0.25F, 1);
                }
                if(!next) {
                    renderer.drawTexture(transforms, Textures.BRIGHTNESS_HIGHLIGHT, 6 + 4*i + 5, dy, 1, 8,
                            0.75F, 0, 1, 1);
                }
            }
        }
        dy += 9;
        transforms.pop();
        // Seasons
        if(AgriApi.getSeasonLogic().isActive()) {
            for(int i = 0; i < this.seasonMask.length; i++) {
                int dx = 70;
                int w = 10;
                int h = 12;
                int x = (i%2)*(w + 2) + 5;
                int y = (i/2)*(h + 2) + 6;
                float v1 = (0.0F + i*h)/48;
                float v2 = (0.0F + (i + 1)*h)/48;
                if(this.seasonMask[i]) {
                    renderer.drawTexture(transforms, Textures.SEASONS_FILLED, x + dx, y + dy, w, h, 0, v1, 1, v2);
                } else {
                    renderer.drawTexture(transforms, Textures.SEASONS_EMPTY, x + dx, y + dy, w, h, 0, v1, 1, v2);
                }
            }
        }
        // Humidity
        for(int i = 0; i < this.humidityMask.length; i++) {
            int dx = Textures.HUMIDITY_OFFSETS[i];
            int w = Textures.HUMIDITY_OFFSETS[i + 1] - Textures.HUMIDITY_OFFSETS[i];
            float u1 = (dx + 0.0F)/53.0F;
            float u2 = (dx + w + 0.0F)/53.0F;
            if(this.humidityMask[i]) {
                renderer.drawTexture(transforms, Textures.HUMIDITY_FILLED, 10 + dx, dy, w, 12, u1, 0, u2, 1);
            } else {
                renderer.drawTexture(transforms, Textures.HUMIDITY_EMPTY, 10 + dx, dy, w, 12, u1, 0, u2, 1);
            }
        }
        dy += 13;
        // Acidity
        for(int i = 0; i < this.acidityMask.length; i++) {
            int dx = Textures.ACIDITY_OFFSETS[i];
            int w = Textures.ACIDITY_OFFSETS[i + 1] - Textures.ACIDITY_OFFSETS[i];
            float u1 = (dx + 0.0F)/53.0F;
            float u2 = (dx + w + 0.0F)/53.0F;
            if(this.acidityMask[i]) {
                renderer.drawTexture(transforms, Textures.ACIDITY_FILLED, 10 + dx, dy, w, 12, u1, 0, u2, 1);
            } else {
                renderer.drawTexture(transforms, Textures.ACIDITY_EMPTY, 10 + dx, dy, w, 12, u1, 0, u2, 1);
            }
        }
        dy += 13;
        // Nutrients
        for(int i = 0; i < this.nutrientsMask.length; i++) {
            int dx = Textures.NUTRIENTS_OFFSETS[i];
            int w = Textures.NUTRIENTS_OFFSETS[i + 1] - Textures.NUTRIENTS_OFFSETS[i];
            float u1 = (dx + 0.0F)/53.0F;
            float u2 = (dx + w + 0.0F)/53.0F;
            if(this.nutrientsMask[i]) {
                renderer.drawTexture(transforms, Textures.NUTRIENTS_FILLED, 10 + dx, dy, w, 12, u1, 0, u2, 1);
            } else {
                renderer.drawTexture(transforms, Textures.NUTRIENTS_EMPTY, 10 + dx, dy, w, 12, u1, 0, u2, 1);
            }
        }
    }

    protected void drawGrowthStages(PageRenderer renderer, MatrixStack transforms) {
        // Position data
        int y0 = 170;
        int delta = 20;
        int rows = this.stages.size()/6 + (this.stages.size() % 6 > 0 ? 1 : 0);
        int columns = this.stages.size()/rows + (this.stages.size() % rows > 0 ? 1 : 0);
        // draw stages
        int row = 0;
        int dx = (renderer.getPageWidth() - (16*(columns)))/(columns + 1);
        for(int i = 0; i < this.stages.size(); i++) {
            int column = i % columns;
            if(i > 0 && column == 0) {
                row += 1;
            }
            renderer.drawTexture(transforms, Textures.GROWTH_STAGE, dx*(column + 1) + 16*column - 1, y0 - delta*(rows - row - 1) - 1, 18, 18);
            transforms.push();
            transforms.translate(0,0, -0.001F);
            this.plant.getGuiRenderer().drawGrowthStage(this.plant, this.stages.get(i), renderer, transforms, dx*(column + 1) + 16*column, y0 - delta*(rows - row - 1), 16, 16);
            transforms.pop();
        }
        // draw text
        renderer.drawText(transforms, GROWTH_STAGES, 10, y0 - delta*rows + 4, 0.90F);
    }

    protected void drawProducts(PageRenderer renderer, MatrixStack transforms) {
        renderer.drawText(transforms, PRODUCTS, 10, 10, 0.80F);
        for(int i = 0; i < this.drops.size(); i++) {
            renderer.drawTexture(transforms, Textures.MUTATION, 10 + i*20, 19, 18, 18, 0, 0, 18.0F/86.0F, 1);
            renderer.drawItem(transforms, this.drops.get(i), 11+i*20, 20);
        }
    }

    protected void drawMutations(PageRenderer renderer, MatrixStack transforms) {
        renderer.drawText(transforms, MUTATIONS, 10, 45, 0.80F);
        int posX = 10;
        int posY = 54;
        int dy = 20;
        for (List<IAgriPlant> plants : this.mutationsOnPage) {
            this.drawMutation(renderer, transforms, posX, posY, plants);
            posY += dy;
        }
    }
}
