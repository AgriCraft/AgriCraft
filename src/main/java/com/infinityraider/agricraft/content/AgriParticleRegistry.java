package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.content.irrigation.SprinklerParticleType;

public class AgriParticleRegistry {
    private static final AgriParticleRegistry INSTANCE = new AgriParticleRegistry();

    public static AgriParticleRegistry getInstance() {
        return INSTANCE;
    }

    public final SprinklerParticleType sprinkler;

    private AgriParticleRegistry() {
        this.sprinkler = new SprinklerParticleType();
    }
}
