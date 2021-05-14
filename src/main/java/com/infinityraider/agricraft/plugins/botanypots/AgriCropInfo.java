package com.infinityraider.agricraft.plugins.botanypots;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthRequirement;
import com.infinityraider.agricraft.api.v1.plant.AgriPlantIngredient;
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
import net.minecraft.util.text.ITextComponent;
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

    public IAgriPlant getPlant() {
        return this.getSeed().getPlant();
    }

    public float getGrowthStatFactor() {
        return this.growthStatFactor;
    }

    @Override
    public AgriPlantIngredient getSeed() {
        return (AgriPlantIngredient) super.getSeed();
    }

    @Override
    public ITextComponent getName() {
        return this.getPlant().getPlantName();
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
            IAgriGrowthRequirement req = this.getPlant().getGrowthRequirement(this.getPlant().getInitialGrowthStage());
            // TODO: use actual plant stats
            if(!req.isSoilHumidityAccepted(soil.getHumidity(), 1)) {
                return -1;
            }
            if(!req.isSoilAcidityAccepted(soil.getAcidity(), 1)) {
                return -1;
            }
            if(!req.isSoilNutrientsAccepted(soil.getNutrients(), 1)) {
                return -1;
            }
            // Fetch and verify Botany Pots modifier
            float modifier = soilInfo.getGrowthModifier();
            if(modifier > -1) {
                // Apply AgriCraft modifier
                modifier = (1 - modifier) * (float) (2 - soil.getGrowthModifier());
                return MathHelper.floor(this.getGrowthTicks() * modifier);
            }
            return -1;
        }).orElse(-1);
    }

    @Override
    public IRecipeSerializer<?> getSerializer () {
        return SERIALIZER;
    }

    private class AgriDisplayState extends DisplayableBlockState {

        public AgriDisplayState() {
            super(AgriCraft.instance.getModBlockRegistry().crop_plant.getDefaultState());
        }

        protected IAgriGrowthStage fetchStage(World world, BlockPos pos) {
            return AgriApi.getCrop(world, pos).map(IAgriCrop::getGrowthStage).orElse(getPlant().getFinalStage());
        }

        @OnlyIn(Dist.CLIENT)
        public void render (World world, BlockPos pos, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay, Direction... preferredSides) {
            BotanyPotsPlantRenderer.getInstance().renderPlant(getPlant(), this.fetchStage(world, pos), matrix, buffer, light, overlay, preferredSides);
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
            AgriPlantIngredient plant = AgriCraft.instance.getModRecipeSerializerRegistry().seed_ingredient.parse(json);
            int growthTicks = json.get("growthTicks").getAsInt();
            float growthStatFactor = json.get("growthStatFactor").getAsFloat();
            return new AgriCropInfo(recipeId, plant, growthTicks, growthStatFactor);
        }

        @Nullable
        @Override
        public AgriCropInfo read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
            AgriPlantIngredient plant = AgriCraft.instance.getModRecipeSerializerRegistry().seed_ingredient.parse(buffer);
            int growthTicks = buffer.readInt();
            float growthStatFactor = buffer.readFloat();
            return new AgriCropInfo(recipeId, plant, growthTicks, growthStatFactor);
        }

        @Override
        public void write(@Nonnull PacketBuffer buffer, @Nonnull AgriCropInfo info) {
            AgriCraft.instance.getModRecipeSerializerRegistry().seed_ingredient.write(buffer, info.getSeed());
            buffer.writeInt(info.getGrowthTicks());
            buffer.writeFloat(info.getGrowthStatFactor());
        }
    }
}
