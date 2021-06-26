package com.infinityraider.agricraft.plugins.botanypots;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.content.items.IAgriSeedItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.plant.AgriPlantIngredient;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import com.infinityraider.agricraft.impl.v1.plant.NoWeed;
import com.infinityraider.agricraft.impl.v1.stats.NoStats;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.darkhax.bookshelf.block.DisplayableBlockState;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.crop.HarvestEntry;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class AgriCropInfo extends CropInfo {
    public static final Serializer SERIALIZER = new Serializer();

    private final float growthStatFactor;
    private final DisplayableBlockState[] display;

    public AgriCropInfo(ResourceLocation id, AgriPlantIngredient plant, int growthTicks, float growthStatFactor) {
        super(id, plant, Collections.emptySet(), growthTicks, Collections.emptyList(), new DisplayableBlockState[]{}, Optional.empty());
        this.growthStatFactor = growthStatFactor;
        this.display = new DisplayableBlockState[]{new AgriDisplayState()};
    }

    public AgriCropInfo withStats(ItemStack seed) {
        if(seed.getItem() instanceof IAgriSeedItem) {
            return ((IAgriSeedItem) seed.getItem()).getStats(seed)
                    .map(stats -> (AgriCropInfo) new WithStats(this, stats))
                    .orElse(this);
        }
        return this;
    }

    public IAgriPlant getPlant() {
        return this.getSeed().getPlant();
    }

    public float getGrowthStatFactor() {
        return this.growthStatFactor;
    }

    protected IAgriStatsMap getStats() {
        return NoStats.getInstance();
    }

    @Override
    public AgriPlantIngredient getSeed() {
        return (AgriPlantIngredient) super.getSeed();
    }

    @Override
    public Set<String> getSoilCategories () {
        // Allow any soil, the growth logic will determine if the crop can grow or not
        return BotanyPotHelper.getManager().getRecipesForType(BotanyPots.instance.getContent().recipeTypeSoil).stream()
                .map(SoilInfo::getCategories)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public List<HarvestEntry> getResults () {
        List<ItemStack> drops = Lists.newArrayList();
        this.getPlant().getAllPossibleClipProducts(drops::add);
        final float size = drops.size();
        if(size > 0) {
            return drops.stream()
                    .map(drop -> new HarvestEntry(1.0F / drops.size(), drop, 1, 1))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public DisplayableBlockState[] getDisplayState() {
        return this.display;
    }

    @Override
    public int getLightLevel(IBlockReader world, BlockPos pos) {
        return 0;
    }

    @Override
    public int getGrowthTicksForSoil(SoilInfo soilInfo) {
        // Correction for farmland
        Block block = soilInfo.getRenderState().getState().getBlock();
        if(block == Blocks.DIRT) {
            block = Blocks.FARMLAND;
        }
        // Run logic
        return AgriApi.getSoilRegistry().valueOf(block).map(soil -> {
            // Fetch and verify Botany Pots modifier
            float modifier = soilInfo.getGrowthModifier();
            if(modifier > -1) {
                // Apply Botany pots soil modifier
                modifier = (1 + modifier * -1);
                // Apply AgriCraft soil modifier
                modifier = modifier * (float) (2 - soil.getGrowthModifier());
                // Apply AgriCraft growth stat modifier
                IAgriGrowthStage stage = this.getPlant().getInitialGrowthStage();
                double base = this.getPlant().getGrowthChanceBase(stage);
                double bonus = this.getPlant().getGrowthChanceBonus(stage);
                int growth = this.getStats().getGrowth();
                modifier =  (2 - (float) (base + growth*bonus))*modifier;
                return MathHelper.floor(this.getGrowthTicks() * modifier);
            }
            return Integer.MAX_VALUE;
        }).orElse(Integer.MAX_VALUE);
    }

    @Override
    public IRecipeSerializer<?> getSerializer () {
        return SERIALIZER;
    }

    public static class WithStats extends AgriCropInfo {
        private final IAgriStatsMap stats;

        public WithStats(AgriCropInfo parent, IAgriStatsMap stats) {
            super(parent.getId(), parent.getSeed(), parent.getGrowthTicks(), parent.getGrowthStatFactor());
            this.stats = stats;
        }

        @Override
        public IAgriStatsMap getStats() {
            return this.stats;
        }
    }

    private class AgriDisplayState extends DisplayableBlockState {

        public AgriDisplayState() {
            super(AgriCraft.instance.getModBlockRegistry().crop_plant.getDefaultState());
        }

        @OnlyIn(Dist.CLIENT)
        public void render (World world, BlockPos pos, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay, Direction... preferredSides) {
            Optional<IAgriCrop> crop = AgriApi.getCrop(world, pos);
            IAgriGrowthStage stage = crop.map(IAgriCrop::getGrowthStage).orElse(getPlant().getFinalStage());
            IAgriWeed weed = crop.map(IAgriCrop::getWeeds).orElse(NoWeed.getInstance());
            IAgriGrowthStage weedStage = crop.map(IAgriCrop::getWeedGrowthStage).orElse(weed.getFinalStage());
            BotanyPotsPlantRenderer.getInstance().renderPlant(
                    getPlant(), stage, weed, weedStage, matrix, buffer, light, overlay, preferredSides);
        }
    }

    private static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AgriCropInfo>, IInfRecipeSerializer {
        private static final String ID = "botany_pots_crop";

        @Override
        public Collection<IInfIngredientSerializer<?>> getIngredientSerializers() {
            return Collections.emptyList();
        }

        @Nonnull
        @Override
        public String getInternalName() {
            return ID;
        }

        @Override
        public boolean isEnabled() {
            return ModList.get().isLoaded(Names.Mods.BOTANY_POTS);
        }

        @Nonnull
        @Override
        public AgriCropInfo read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            if(!json.has("plant")) {
                throw new JsonParseException("Agricraft botany pots crop must have a \"plant\" property");
            }
            if(!json.has("growthTicks")) {
                throw new JsonParseException("Agricraft botany pots crop must have a \"growthTicks\" property");
            }
            if(!json.has("growthStatFactor")) {
                throw new JsonParseException("Agricraft botany pots crop must have a \"growthStatFactor\" property");
            }
            AgriPlantIngredient plant = AgriCraft.instance.getModRecipeSerializerRegistry().plant_ingredient.parse(json);
            int growthTicks = json.get("growthTicks").getAsInt();
            float growthStatFactor = json.get("growthStatFactor").getAsFloat();
            return new AgriCropInfo(recipeId, plant, growthTicks, growthStatFactor);
        }

        @Nullable
        @Override
        public AgriCropInfo read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
            AgriPlantIngredient plant = AgriCraft.instance.getModRecipeSerializerRegistry().plant_ingredient.parse(buffer);
            int growthTicks = buffer.readInt();
            float growthStatFactor = buffer.readFloat();
            return new AgriCropInfo(recipeId, plant, growthTicks, growthStatFactor);
        }

        @Override
        public void write(@Nonnull PacketBuffer buffer, @Nonnull AgriCropInfo info) {
            AgriCraft.instance.getModRecipeSerializerRegistry().plant_ingredient.write(buffer, info.getSeed());
            buffer.writeInt(info.getGrowthTicks());
            buffer.writeFloat(info.getGrowthStatFactor());
        }
    }
}
