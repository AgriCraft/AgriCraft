package com.infinityraider.agricraft.content.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeedIngredient;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class AgriSeedIngredientSerializer implements IInfIngredientSerializer<AgriSeedIngredient> {
    private static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "seed");

    public AgriSeedIngredientSerializer() {}

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Nonnull
    @Override
    public AgriSeedIngredient parse(PacketBuffer buffer) {
        return new AgriSeedIngredient(
                AgriApi.getPlantRegistry().get(buffer.readString())
                        .orElse(AgriApi.getPlantRegistry().getNoPlant())
        );
    }

    @Nonnull
    @Override
    public AgriSeedIngredient parse(JsonObject json) {
        if(!json.has("plant")) {
            throw new JsonParseException("Agricraft plant ingredient must have a \"plant\" property!");
        }
        String id = json.get("plant").getAsString();
        IAgriPlant plant = AgriApi.getPlantRegistry().get(id).orElse(AgriApi.getPlantRegistry().getNoPlant());
        if(!plant.isPlant()) {
            throw new JsonParseException("Invalid plant on Agricraft plant ingredient: " + id);
        }
        return new AgriSeedIngredient(plant);
    }

    @Override
    public void write(@Nonnull PacketBuffer buffer, @Nonnull AgriSeedIngredient ingredient) {
        buffer.writeString(ingredient.getPlant().getId());
    }
}
