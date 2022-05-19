package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.sound.SoundEventBase;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;

public final class AgriSoundRegistry extends ModContentRegistry implements IAgriContent.Sounds {
    private static final AgriSoundRegistry INSTANCE = new AgriSoundRegistry();

    public static AgriSoundRegistry getInstance() {
        return INSTANCE;
    }

    public final RegistryInitializer<SoundEventBase> valve;

    private AgriSoundRegistry() {
        super();
        this.valve = this.sound(() -> new SoundEventBase(AgriCraft.instance.getModId(), Names.Sounds.VALVE) {
            @Override
            public boolean isEnabled() {
                return true;
            }
        });
    }

    @Override
    public SoundEventBase getValveSound() {
        return valve.get();
    }
}
