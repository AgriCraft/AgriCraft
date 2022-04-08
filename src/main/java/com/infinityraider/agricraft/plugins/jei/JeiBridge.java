package com.infinityraider.agricraft.plugins.jei;

import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.capability.CapabilityResearchedPlants;
import com.infinityraider.agricraft.reference.Names;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

public final class JeiBridge {
    public static void unHideMutations(Collection<IAgriMutation> mutations) {
        if(isLoaded()) {
            JeiPlugin.unHideMutations(mutations);
        }
    }

    public static void hideMutations(Collection<IAgriMutation> mutations) {
        if(isLoaded()) {
            JeiPlugin.hideMutations(mutations);
        }
    }

    public static void hideAndUnhideMutations(@Nullable Player player) {
        if(player == null) {
            return;
        }
        if (AgriCraft.instance.getConfig().progressiveJEI()) {
            Set<IAgriMutation> toHide = Sets.newIdentityHashSet();
            Set<IAgriMutation> toShow = Sets.newIdentityHashSet();
            AgriApi.getMutationRegistry()
                    .stream()
                    .forEach(mutation -> {
                        if (CapabilityResearchedPlants.getInstance().isMutationResearched(player, mutation)) {
                            toShow.add(mutation);
                        } else {
                            toHide.add(mutation);
                        }
                    });
            hideMutations(toHide);
            unHideMutations(toShow);
        }
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(Names.Mods.JEI);
    }
}
