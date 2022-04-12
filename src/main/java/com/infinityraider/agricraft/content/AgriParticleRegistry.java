package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.content.irrigation.SprinklerParticleType;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;

public final class AgriParticleRegistry extends ModContentRegistry {
    private static final AgriParticleRegistry INSTANCE = new AgriParticleRegistry();

    public static AgriParticleRegistry getInstance() {
        return INSTANCE;
    }

    public final RegistryInitializer<SprinklerParticleType> sprinkler;

    private AgriParticleRegistry() {
        super();
        this.sprinkler = this.particle(SprinklerParticleType::new);
    }
}
