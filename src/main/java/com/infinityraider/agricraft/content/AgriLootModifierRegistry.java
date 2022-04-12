package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.content.world.LootModifierGrassDrops;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;

public final class AgriLootModifierRegistry extends ModContentRegistry {
    private static final AgriLootModifierRegistry INSTANCE = new AgriLootModifierRegistry();

    public static AgriLootModifierRegistry getInstance() {
        return INSTANCE;
    }

    public final RegistryInitializer<LootModifierGrassDrops.Serializer> grass_drops;

    private AgriLootModifierRegistry() {
        super();
        this.grass_drops = this.loot(LootModifierGrassDrops::getSerializer);
    }
}
