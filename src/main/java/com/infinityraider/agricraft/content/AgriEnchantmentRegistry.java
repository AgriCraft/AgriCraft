package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.content.tools.*;

public final class AgriEnchantmentRegistry {
    public static final IAgriContent.Enchantments ACCESSOR = new Accessor();

    public static final EnchantmentSeedBag SEED_BAG = new EnchantmentSeedBag();

    private static final class Accessor implements IAgriContent.Enchantments {
        private Accessor() {
        }

        @Override
        public EnchantmentSeedBag getSeedBagEnchantment() {
            return SEED_BAG;
        }
    }
}
