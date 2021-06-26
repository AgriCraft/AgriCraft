package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.sound.SoundEventBase;

public class AgriSoundRegistry implements IAgriContent.Sounds {
    private static final AgriSoundRegistry INSTANCE = new AgriSoundRegistry();

    public static AgriSoundRegistry getInstance() {
        return INSTANCE;
    }

    public final SoundEventBase valve;

    private AgriSoundRegistry() {
        this.valve = new SoundEventBase(AgriCraft.instance.getModId(), Names.Sounds.VALVE) {
            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }

    @Override
    public SoundEventBase getValveSound() {
        return this.valve;
    }
}
