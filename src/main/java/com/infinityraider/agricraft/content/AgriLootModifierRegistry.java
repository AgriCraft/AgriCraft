package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.content.world.LootModifierGrassDrops;

public class AgriLootModifierRegistry {
    private static final AgriLootModifierRegistry INSTANCE = new AgriLootModifierRegistry();

    public static AgriLootModifierRegistry getInstance() {
        return INSTANCE;
    }

    public final LootModifierGrassDrops.Serializer grass_drops;

    private AgriLootModifierRegistry() {
        this.grass_drops = LootModifierGrassDrops.getSerializer();
    }
}
