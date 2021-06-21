package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.content.tools.*;
import net.minecraft.enchantment.Enchantment;

public class AgriEnchantmentRegistry implements IAgriContent.Enchantments {

    private static final AgriEnchantmentRegistry INSTANCE = new AgriEnchantmentRegistry();

    public static AgriEnchantmentRegistry getInstance() {
        return INSTANCE;
    }

    public final EnchantmentSeedBag seed_bag;

    @SuppressWarnings("deprecation")
    private AgriEnchantmentRegistry() {
        this.seed_bag = new EnchantmentSeedBag();
    }

    @Override
    public Enchantment getSeedBagEnchantment() {
        return this.seed_bag;
    }
}
