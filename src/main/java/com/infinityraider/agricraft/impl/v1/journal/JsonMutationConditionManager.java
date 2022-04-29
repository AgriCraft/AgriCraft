package com.infinityraider.agricraft.impl.v1.journal;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.genetics.IJsonMutationCondition;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class JsonMutationConditionManager {
    private static final Map<String, IJsonMutationCondition.Factory> triggers = Maps.newConcurrentMap();

    public static Optional<IJsonMutationCondition.Factory> get(String id) {
        return Optional.ofNullable(triggers.get(id));
    }

    public static boolean register(IJsonMutationCondition.Factory factory) {
        Objects.requireNonNull(factory);

        Preconditions.checkArgument(!triggers.containsKey(factory.getId()), "Can not create two triggers with identical ids:" + factory.getId());
        if(triggers.containsKey(factory.getId())) {
            return false;
        }
        triggers.put(factory.getId(), factory);
        return true;
    }

    private JsonMutationConditionManager() {}
}
