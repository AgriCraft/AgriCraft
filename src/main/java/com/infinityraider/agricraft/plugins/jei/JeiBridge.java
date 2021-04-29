package com.infinityraider.agricraft.plugins.jei;

import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.reference.Names;
import net.minecraftforge.fml.ModList;

public final class JeiBridge {
    public static void unHideMutation(IAgriMutation mutation) {
        if(isLoaded()) {
            JeiPlugin.unHideMutation(mutation);
        }
    }

    public static void hideMutation(IAgriMutation mutation) {
        if(isLoaded()) {
            JeiPlugin.hideMutation(mutation);
        }
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(Names.Mods.JEI);
    }
}
