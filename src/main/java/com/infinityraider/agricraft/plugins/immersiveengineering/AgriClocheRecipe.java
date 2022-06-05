package com.infinityraider.agricraft.plugins.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.ClocheRecipe;
import blusunrize.immersiveengineering.api.crafting.ClocheRenderFunction;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriSeedItem;
import com.infinityraider.agricraft.api.v1.plant.AgriPlantIngredient;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.requirement.AnySoilIngredient;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthRequirement;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import com.infinityraider.agricraft.content.AgriRecipeSerializerRegistry;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.impl.v1.requirement.NoSoil;
import com.infinityraider.agricraft.impl.v1.stats.NoStats;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.util.Lazy;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class AgriClocheRecipe extends ClocheRecipe {
    private static final ItemStack FARMLAND = new ItemStack(Items.FARMLAND);

    public static final Serializer SERIALIZER = new Serializer();
    private static final Random RANDOM = new Random();

    private static final ClocheRenderFunction.ClocheRenderReference RENDER_REFERENCE =
            new ClocheRenderFunction.ClocheRenderReference(
                    AgriCraft.instance.getModId(),
                    AgriApi.getAgriContent().getBlocks().getCropBlock()
            );

    private static List<Lazy<ItemStack>> getRealOutputs(AgriPlantIngredient seed) {
        List<Lazy<ItemStack>> products = Lists.newArrayList();
        seed.getPlant().getAllPossibleProducts(stack -> products.add(Lazy.of(() -> stack)));
        return products;
    }

    private static List<Lazy<ItemStack>> getTempOutputs(AgriPlantIngredient seed) {
        return Lists.newArrayList(Lazy.of(() -> {
            List<ItemStack> stacks = Lists.newArrayList();
            seed.getPlant().getAllPossibleProducts(stacks::add);
            return stacks.stream().findFirst().orElse(ItemStack.EMPTY);
        }));
    }

    private final AgriPlantIngredient seed;
    private final float growthStatFactor;

    public AgriClocheRecipe(ResourceLocation id, AgriPlantIngredient seed, int time, float growthStatFactor) {
        super(id, getTempOutputs(seed), seed, AnySoilIngredient.getInstance(), time, RENDER_REFERENCE);
        this.seed = seed;
        this.growthStatFactor = growthStatFactor;
    }

    void updateOutputs() {
        this.outputs.clear();
        this.outputs.addAll(getRealOutputs(this.getSeed()));
    }

    public AgriPlantIngredient getSeed() {
        return this.seed;
    }

    public int getGrowthTicks() {
        return super.time;
    }

    public float getGrowthStatFactor() {
        return this.growthStatFactor;
    }


    protected IAgriPlant getPlant(ItemStack seed) {
        if(seed.getItem() instanceof IAgriSeedItem) {
            return ((IAgriSeedItem) seed.getItem()).getPlant(seed);
        }
        return NoPlant.getInstance();
    }

    protected Optional<IAgriStatsMap> getStats(ItemStack seed) {
        if(seed.getItem() instanceof IAgriSeedItem) {
            return ((IAgriSeedItem) seed.getItem()).getStats(seed);
        }
        return Optional.empty();
    }

    protected IAgriSoil getSoil(ItemStack soil) {
        if(soil.getItem() == Items.DIRT) {
            // correction for farmland
            return AgriApi.getSoilRegistry().valueOf(Blocks.FARMLAND).orElse(NoSoil.getInstance());
        }
        return AgriApi.getSoilRegistry().valueOf(soil).orElse(NoSoil.getInstance());
    }

    @Override
    public boolean matches(ItemStack seed, ItemStack soil) {
        return super.seed.test(seed) && (super.soil.test(soil) || super.soil.test(this.transformSoil(soil)));
    }

    protected ItemStack transformSoil(ItemStack soil) {
        if(soil.isEmpty()) {
            return soil;
        }
        if(soil.getItem() == Items.DIRT) {
            return FARMLAND;
        }
        return soil;
    }

    @Override
    public List<Lazy<ItemStack>> getOutputs(ItemStack seed, ItemStack soil) {
        List<ItemStack> outputs = Lists.newArrayList();
        IAgriPlant plant = this.getPlant(seed);
        if(plant.isPlant()) {
            this.getPlant(seed).getHarvestProducts(
                    outputs::add,
                    plant.getFinalStage(),
                    this.getStats(seed).orElse(NoStats.getInstance()),
                    RANDOM
            );
        }
        return outputs.stream().map(stack -> Lazy.of(() -> stack)).collect(Collectors.toList());
    }

    @Override
    public int getTime(ItemStack seed, ItemStack soilStack) {
        IAgriPlant plant = this.getPlant(seed);
        IAgriSoil soil = this.getSoil(soilStack);
        Optional<IAgriStatsMap> statsOptional = this.getStats(seed);
        if(plant.isPlant() && soil.isSoil() && statsOptional.isPresent()) {
            IAgriStatsMap stats = statsOptional.get();
            IAgriGrowthRequirement req = plant.getGrowthRequirement(plant.getInitialGrowthStage());
            int strength = stats.getStrength();
            if(!req.getSoilHumidityResponse(soil.getHumidity(), strength).isFertile()) {
                return Integer.MAX_VALUE;
            }
            if(!req.getSoilAcidityResponse(soil.getAcidity(), strength).isFertile()) {
                return Integer.MAX_VALUE;
            }
            if(!req.getSoilNutrientsResponse(soil.getNutrients(), strength).isFertile()) {
                return Integer.MAX_VALUE;
            }
            double growthFactor = 1 - stats.getGrowth()*this.getGrowthStatFactor();
            double soilFactor = 2 - soil.getGrowthModifier();
            return Math.max((int) (this.getGrowthTicks()*growthFactor*soilFactor), 1);
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public ItemStack getResultItem() {
        MutableObject<ItemStack> product = new MutableObject<>();
        this.getSeed().getPlant().getAllPossibleProducts(stack -> {
            if(product.getValue() == null) {
                product.setValue(stack);
            }
        });
        ItemStack result = product.getValue();
        return result == null ? ItemStack.EMPTY : result;
    }

    @Override
    protected IERecipeSerializer<ClocheRecipe> getIESerializer() {
        return SERIALIZER;
    }

    private static class Serializer extends IERecipeSerializer<ClocheRecipe> implements RecipeSerializer<ClocheRecipe>, IInfRecipeSerializer<ClocheRecipe> {
        private static final String ID = "agri_cloche_recipe";

        @Nonnull
        @Override
        public String getInternalName() {
            return ID;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public Collection<IInfIngredientSerializer<?>> getIngredientSerializers() {
            return Collections.emptyList();
        }

        @Override
        public ItemStack getIcon() {
            return new ItemStack(AgriApi.getAgriContent().getItems().getDebuggerItem());
        }

        @Override
        public AgriClocheRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            if(!json.has("plant")) {
                throw new JsonParseException("Agricraft botany pots crop must have a \"plant\" property");
            }
            if(!json.has("growthTicks")) {
                throw new JsonParseException("Agricraft botany pots crop must have a \"growthTicks\" property");
            }
            if(!json.has("growthStatFactor")) {
                throw new JsonParseException("Agricraft botany pots crop must have a \"growthStatFactor\" property");
            }
            AgriPlantIngredient plant = AgriRecipeSerializerRegistry.getInstance().plant_ingredient.parse(json);
            int growthTicks = json.get("growthTicks").getAsInt();
            float growthStatFactor = json.get("growthStatFactor").getAsFloat();
            Map<String, ClocheRenderFunction.ClocheRenderFunctionFactory> RENDER_FUNCTION_FACTORIES = ClocheRenderFunction.RENDER_FUNCTION_FACTORIES;
            return new AgriClocheRecipe(recipeId, plant, growthTicks, growthStatFactor);
        }

        @Override
        public AgriClocheRecipe readFromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json, ICondition.IContext context) {
            return this.fromJson(recipeId, json);
        }

        @Nullable
        @Override
        public AgriClocheRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer) {
            if(buffer.readBoolean()) {
                AgriPlantIngredient plant = AgriRecipeSerializerRegistry.getInstance().plant_ingredient.parse(buffer);
                int growthTicks = buffer.readInt();
                float growthStatFactor = buffer.readFloat();
                return new AgriClocheRecipe(recipeId, plant, growthTicks, growthStatFactor);
            } else {
                return null;
            }
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull ClocheRecipe clocheRecipe) {
            if(clocheRecipe instanceof AgriClocheRecipe) {
                AgriClocheRecipe recipe = (AgriClocheRecipe) clocheRecipe;
                buffer.writeBoolean(true);
                AgriRecipeSerializerRegistry.getInstance().plant_ingredient.write(buffer, recipe.getSeed());
                buffer.writeInt(recipe.getGrowthTicks());
                buffer.writeFloat(recipe.getGrowthStatFactor());
            } else {
                buffer.writeBoolean(false);
            }
        }
    }

}
