package com.infinityraider.agricraft.content.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.infinitylib.loot.IInfLootModifierSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LootModifierGrassDrops extends LootModifier {
    private static final Serializer SERIALIZER = new Serializer();

    public static Serializer getSerializer() {
        return SERIALIZER;
    }

    private final boolean reset;
    private final Entry[] entries;
    private final int totalWeight;

    protected LootModifierGrassDrops(ILootCondition[] conditions, boolean reset, Entry[] entries) {
        super(conditions);
        this.reset = reset;
        this.entries = entries;
        this.totalWeight = Arrays.stream(entries).mapToInt(Entry::getWeight).sum();
    }

    protected boolean reset() {
        return this.reset;
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        if(this.reset()) {
            generatedLoot.clear();
        }
        if(generatedLoot.isEmpty() && this.entries.length > 0) {
            Entry entry = this.selectRandomEntry(context.getRandom());
            if(entry != null) {
                ItemStack stack = entry.generateSeed(context.getRandom());
                if(!stack.isEmpty()) {
                    generatedLoot.add(stack);
                }
            }
        }
        return generatedLoot;
    }

    @Nullable
    protected Entry selectRandomEntry(Random random) {
        int selector = random.nextInt(this.totalWeight);
        for (Entry entry : this.entries) {
            if (entry.getWeight() >= selector) {
                if (entry.getPlant() != null) {
                    return entry;
                }
            }
            selector -= entry.weight;
        }
        return null;
    }

    public static class Entry {
        private final String plantId;
        private final int statsMin;
        private final int statsMax;
        private final int weight;

        private IAgriPlant plant;

        public Entry(String plantId, int min, int max, int weight) {
            this.plantId = plantId;
            this.statsMin = min;
            this.statsMax = max;
            this.weight = weight;
        }

        public String getPlantId() {
            return this.plantId;
        }

        @Nullable
        public IAgriPlant getPlant() {
            if(this.plant == null) {
                this.plant = AgriApi.getPlantRegistry().get(this.getPlantId()).orElse(null);
            }
            return this.plant;
        }

        public int getStatsMin() {
            return this.statsMin;
        }

        public int getStatsMax() {
            return this.statsMax;
        }

        public int generateStat(Random random) {
            return random.nextInt(this.getStatsMax() - this.getStatsMin() + 1) + this.getStatsMin();
        }

        public int getWeight() {
            return this.weight;
        }

        public ItemStack generateSeed(Random random) {
            IAgriPlant plant = this.getPlant();
            if(plant == null) {
                return ItemStack.EMPTY;
            }
            return AgriApi.getAgriGenomeBuilder(plant)
                    .randomStats(stat -> this.generateStat(random))
                    .build()
                    .toSeedStack();
        }
    }

    public static final class Serializer extends GlobalLootModifierSerializer<LootModifierGrassDrops> implements IInfLootModifierSerializer {
        @Nonnull
        @Override
        public String getInternalName() {
            return "grass_seed_drops";
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public LootModifierGrassDrops read(ResourceLocation location, JsonObject object, ILootCondition[] conditions) {
            return new LootModifierGrassDrops(
                    conditions,
                    object.get("reset").getAsBoolean(),
                    this.readEntries(object.get("seeds").getAsJsonArray())
            );
        }

        protected Entry[] readEntries(JsonArray array) {
            Entry[] entries = new Entry[array.size()];
            for(int i = 0; i < entries.length; i++) {
                entries[i] = this.readEntry(array.get(i).getAsJsonObject());
            }
            return entries;
        }

        protected Entry readEntry(JsonObject object) {
            String plant = object.get("plant").getAsString();
            JsonArray statsArray = object.get("stats").getAsJsonArray();
            if(statsArray.size() != 2) {
                throw new JsonSyntaxException("The \"stats\" field in an agricraft:grass_drops seed entry must contain 2 numbers");
            }
            int min = statsArray.get(0).getAsInt();
            int max = statsArray.get(1).getAsInt();
            if(min > max) {
                throw new JsonSyntaxException("The first value in the \"stats\" field in an agricraft:grass_drops seed entry must not be larger than the second");
            }
            int weight = object.get("weight").getAsInt();
            return new Entry(plant, min, max, weight);
        }

        @Override
        public JsonObject write(LootModifierGrassDrops instance) {
            JsonObject json = super.makeConditions(instance.conditions);
            json.addProperty("reset", instance.reset());
            JsonArray entries = new JsonArray();
            Arrays.stream(instance.entries).map(this::writeEntry).forEach(entries::add);
            return json;
        }

        protected JsonObject writeEntry(Entry entry) {
            JsonObject json = new JsonObject();
            json.addProperty("plant", entry.getPlantId());
            JsonArray statsArray = new JsonArray();
            statsArray.add(entry.getStatsMin());
            statsArray.add(entry.getStatsMax());
            json.add("stats", statsArray);
            json.addProperty("weight", entry.getWeight());
            return json;
        }
    }
}
