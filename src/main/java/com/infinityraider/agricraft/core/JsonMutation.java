/*
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.plant.AgriMutation;
import com.infinityraider.agricraft.apiimpl.PlantRegistry;
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
                PlantRegistry.getInstance().getPlant(mutation.getChild().getId()),
                PlantRegistry.getInstance().getPlant(mutation.getParent1().getId()),
                PlantRegistry.getInstance().getPlant(mutation.getParent2().getId())
        );
        this.mutation = mutation;
    }

}
