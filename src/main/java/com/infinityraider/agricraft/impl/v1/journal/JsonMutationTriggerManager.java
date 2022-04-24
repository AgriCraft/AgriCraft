package com.infinityraider.agricraft.impl.v1.journal;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.infinityraider.agricraft.api.v1.genetics.IJsonMutationTrigger;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class JsonMutationTriggerManager {
    private static final Map<String, IJsonMutationTrigger.Factory> triggers = Maps.newConcurrentMap();

    public static Optional<IJsonMutationTrigger.Factory> get(String id) {
        return Optional.ofNullable(triggers.get(id));
    }

    public static boolean register(IJsonMutationTrigger.Factory factory) {
        Objects.requireNonNull(factory);

        Preconditions.checkArgument(!triggers.containsKey(factory.getId()), "Can not create two triggers with identical ids:" + factory.getId());
        if(triggers.containsKey(factory.getId())) {
            return false;
        }
        triggers.put(factory.getId(), factory);
        return true;
    }

    private JsonMutationTriggerManager() {}
}
