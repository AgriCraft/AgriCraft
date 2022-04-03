package com.infinityraider.agricraft.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;

import java.util.function.Predicate;

public class NBTFilter implements Predicate<CompoundNBT> {
    private final String key;
    private final Predicate<INBT> test;

    @SuppressWarnings("unchecked")
    public NBTFilter(String key, Predicate<? extends INBT> test) {
        this.key = key;
        this.test = (Predicate<INBT>) test;
    }

    public NBTFilter(String key, String value) {
        this(key, tag -> tag.getString().equals(value));
    }

    @Override
    public boolean test(CompoundNBT tag) {
        return tag.contains(this.key)  && this.test.test(tag.get(this.key));
    }

    public static NBTFilter fromJson(JsonObject json) {
        String key = json.get("key").getAsString();
        JsonElement test = json.get("test");
        if(test.isJsonObject()) {
            return new NBTFilter(key, fromJson(test.getAsJsonObject()));
        } else {
            return new NBTFilter(key, test.getAsString());
        }
    }
}
