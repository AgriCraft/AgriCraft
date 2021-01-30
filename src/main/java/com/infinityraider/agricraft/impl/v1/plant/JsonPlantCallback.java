package com.infinityraider.agricraft.impl.v1.plant;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonPlantCallback implements IJsonPlantCallback {
    private static final Map<String, IJsonPlantCallback> callbacks = Maps.newConcurrentMap();

    public static Optional<IJsonPlantCallback> get(String id) {
        return Optional.ofNullable(callbacks.get(id));
    }

    public static List<IJsonPlantCallback> get(List<String> ids) {
        return ids.stream().map(JsonPlantCallback::get).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    public static boolean register(IJsonPlantCallback callback) {
        Objects.requireNonNull(callback);
        if(callbacks.containsKey(callback.getId())) {
            return false;
        }
        callbacks.put(callback.getId(), callback);
        return true;
    }

    private final String id;

    protected JsonPlantCallback(String id) {
        Preconditions.checkArgument(!callbacks.containsKey(id), "Can not create two callbacks with identical ids:" + id);
        this.id = id;
        callbacks.put(id, this);
    }

    @Override
    public final String getId() {
        return this.id;
    }
}
