package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.sound.SoundEventBase;

public final class AgriSoundRegistry {
    public static final IAgriContent.Sounds ACCESSOR = new Accessor();

    public static final SoundEventBase VALVE = new SoundEventBase(AgriCraft.instance.getModId(), Names.Sounds.VALVE) {
        @Override
        public boolean isEnabled() {
            return true;
        }
    };

    private static final class Accessor implements IAgriContent.Sounds {
        private Accessor() {
        }

        @Override
        public SoundEventBase getValveSound() {
            return VALVE;
        }
    }
}
