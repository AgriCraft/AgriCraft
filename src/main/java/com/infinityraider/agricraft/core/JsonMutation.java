/*
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.plant.AgriMutation;
import com.infinityraider.agricraft.api.AgriApi;
import com.infinityraider.agricraft.farming.mutation.Mutation;

/**
 *
 *
 */
public class JsonMutation extends Mutation {

    public final AgriMutation mutation;

    public JsonMutation(AgriMutation mutation) {
        super(
                mutation.getChance(),
                AgriApi.PlantRegistry().get().getPlant(mutation.getChild().getId()),
                AgriApi.PlantRegistry().get().getPlant(mutation.getParent1().getId()),
                AgriApi.PlantRegistry().get().getPlant(mutation.getParent2().getId())
        );
        this.mutation = mutation;
    }

}
