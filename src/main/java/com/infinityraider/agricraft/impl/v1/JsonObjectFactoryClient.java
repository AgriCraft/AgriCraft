package com.infinityraider.agricraft.impl.v1;

import com.agricraft.agricore.templates.*;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlant;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlantClient;
import com.infinityraider.agricraft.impl.v1.plant.JsonWeed;
import com.infinityraider.agricraft.impl.v1.plant.JsonWeedClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JsonObjectFactoryClient extends JsonObjectFactory {
    private static final JsonObjectFactory INSTANCE = new JsonObjectFactoryClient();

    public static JsonObjectFactory getInstance() {
        return INSTANCE;
    }

    private JsonObjectFactoryClient() {}

    @Override
    public JsonPlant createPlant(AgriPlant plant) {
        return new JsonPlantClient(plant);
    }

    @Override
    public JsonWeed createWeed(AgriWeed weed) {
        return new JsonWeedClient(weed);
    }
}
