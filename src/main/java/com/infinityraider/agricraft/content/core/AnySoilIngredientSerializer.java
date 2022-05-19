package com.infinityraider.agricraft.content.core;

import com.google.gson.JsonObject;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.requirement.AnySoilIngredient;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class AnySoilIngredientSerializer  implements IInfIngredientSerializer<AnySoilIngredient> {
    private static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "any_soil");

    public AnySoilIngredientSerializer() {}

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Nonnull
    @Override
    public AnySoilIngredient parse(@Nonnull FriendlyByteBuf buffer) {
        return AnySoilIngredient.getInstance();
    }

    @Nonnull
    @Override
    public AnySoilIngredient parse(@Nonnull JsonObject json) {
        return AnySoilIngredient.getInstance();
    }

    @Override
    public void write(@Nonnull FriendlyByteBuf buffer, @Nonnull AnySoilIngredient ingredient) {

    }
}
