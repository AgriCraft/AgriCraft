package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.content.tools.*;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;

public final class AgriEnchantmentRegistry extends ModContentRegistry implements IAgriContent.Enchantments {
    private static final AgriEnchantmentRegistry INSTANCE = new AgriEnchantmentRegistry();

    public static AgriEnchantmentRegistry getInstance() {
        return INSTANCE;
    }

    public final RegistryInitializer<EnchantmentSeedBag> seed_bag;

    private AgriEnchantmentRegistry() {
        super();
        this.seed_bag = this.enchantment(EnchantmentSeedBag::new);
    }

    @Override
    public EnchantmentSeedBag getSeedBagEnchantment() {
        return this.seed_bag.get();
    }
}
