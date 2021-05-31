package com.infinityraider.agricraft.content.core;

import com.google.gson.JsonObject;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.requirement.AnySoilIngredient;
import com.infinityraider.infinitylib.crafting.IInfIngredientSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

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
    public AnySoilIngredient parse(@Nonnull PacketBuffer buffer) {
        return AnySoilIngredient.getInstance();
    }

    @Nonnull
    @Override
    public AnySoilIngredient parse(@Nonnull JsonObject json) {
        return AnySoilIngredient.getInstance();
    }

    @Override
    public void write(@Nonnull PacketBuffer buffer, @Nonnull AnySoilIngredient ingredient) {

    }
}
