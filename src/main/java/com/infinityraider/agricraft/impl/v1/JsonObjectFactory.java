package com.infinityraider.agricraft.impl.v1;

import com.agricraft.agricore.templates.AgriFertilizer;
import com.agricraft.agricore.templates.AgriPlant;
import com.agricraft.agricore.templates.AgriSoil;
import com.agricraft.agricore.templates.AgriWeed;
import com.infinityraider.agricraft.impl.v1.fertilizer.JsonFertilizer;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlant;
import com.infinityraider.agricraft.impl.v1.plant.JsonWeed;
import com.infinityraider.agricraft.impl.v1.requirement.JsonSoil;

public class JsonObjectFactory {
    private static final JsonObjectFactory INSTANCE = new JsonObjectFactory();

    public static JsonObjectFactory getInstance() {
        return INSTANCE;
    }

    protected JsonObjectFactory() {}

    public JsonPlant createPlant(AgriPlant plant) {
        return new JsonPlant(plant);
    }

    public JsonWeed createWeed(AgriWeed weed) {
        return new JsonWeed(weed);
    }

    public JsonSoil createSoil(AgriSoil soil) {
        return new JsonSoil(soil);
    }

    public JsonFertilizer createFertilizer(AgriFertilizer fertilizer) {
        return new JsonFertilizer(fertilizer);
    }
}
