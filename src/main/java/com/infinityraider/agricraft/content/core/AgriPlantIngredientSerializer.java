package com.infinityraider.agricraft.content.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.AgriPlantIngredient;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class AgriPlantIngredientSerializer implements IInfIngredientSerializer<AgriPlantIngredient> {
    private static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "seed");

    public AgriPlantIngredientSerializer() {}

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Nonnull
    @Override
    public AgriPlantIngredient parse(FriendlyByteBuf buffer) {
        String id = buffer.readUtf();
        IAgriPlant plant = AgriApi.getPlantRegistry().get(id).orElse(AgriApi.getPlantRegistry().getNoPlant());
        if(plant.isPlant()) {
            return new AgriPlantIngredient(plant);
        } else {
            return new AgriLazyPlantIngredient(id);
        }
    }

    @Nonnull
    @Override
    public AgriPlantIngredient parse(JsonObject json) {
        if(!json.has("plant")) {
            throw new JsonParseException("Agricraft plant ingredient must have a \"plant\" property!");
        }
        String id = json.get("plant").getAsString();
        IAgriPlant plant = AgriApi.getPlantRegistry().get(id).orElse(AgriApi.getPlantRegistry().getNoPlant());
        if(plant.isPlant()) {
            return new AgriPlantIngredient(plant);
        } else {
            return new AgriLazyPlantIngredient(id);
        }
    }

    @Override
    public void write(@Nonnull FriendlyByteBuf buffer, @Nonnull AgriPlantIngredient ingredient) {
        buffer.writeUtf(ingredient.getPlantId());
    }

    public static class AgriLazyPlantIngredient extends AgriPlantIngredient {
        private final String plantId;
        private IAgriPlant plant;

        public AgriLazyPlantIngredient(String plant) {
            super(AgriApi.getPlantRegistry().getNoPlant());
            this.plantId = plant;
        }

        @Override
        public String getPlantId() {
            return this.plantId;
        }

        @Override
        public IAgriPlant getPlant() {
            if (this.plant == null) {
                IAgriPlant plant = AgriApi.getPlantRegistry().get(this.getPlantId()).orElse(super.getPlant());
                if (plant.isPlant()) {
                    this.plant = plant;
                } else {
                    return plant;
                }
            }
            return this.plant;
        }
    }
}
