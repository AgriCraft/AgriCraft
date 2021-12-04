package com.infinityraider.agricraft.impl.v1.plant;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class JsonPlantCallbackManager {
    private static final String ID = "id";
    private static final Map<String, IJsonPlantCallback.Factory> callbacks = Maps.newConcurrentMap();

    public static Optional<IJsonPlantCallback.Factory> get(String id) {
        return Optional.ofNullable(callbacks.get(id));
    }

    public static Optional<IJsonPlantCallback.Factory> get(JsonElement json) {
        if(json instanceof JsonPrimitive) {
            JsonPrimitive primitive = (JsonPrimitive) json;
            if(primitive.isString()) {
                return get(primitive.getAsString());
            }
        }
        if(json instanceof JsonObject) {
            JsonObject obj = (JsonObject) json;
            if(obj.has(ID)) {
                return get(obj.get(ID).getAsString());
            }
        }
        return Optional.empty();
    }

    public static boolean register(IJsonPlantCallback.Factory callback) {
        Objects.requireNonNull(callback);

        Preconditions.checkArgument(!callbacks.containsKey(callback.getId()), "Can not create two callbacks with identical ids:" + callback.getId());
        if(callbacks.containsKey(callback.getId())) {
            return false;
        }
        callbacks.put(callback.getId(), callback);
        return true;
    }

    private JsonPlantCallbackManager() {}
}
