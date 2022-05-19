package com.infinityraider.agricraft.impl.v1;

import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.content.*;

public final class AgriContent implements IAgriContent {
    private static final AgriContent INSTANCE = new AgriContent();

    public static AgriContent getInstance() {
        return INSTANCE;
    }

    private AgriContent() {}

    @Override
    public Blocks getBlocks() {
        return AgriBlockRegistry.getInstance();
    }

    @Override
    public Enchantments getEnchantments() {
        return AgriEnchantmentRegistry.getInstance();
    }

    @Override
    public Fluids getFluids() {
        return AgriFluidRegistry.getInstance();
    }

    @Override
    public Items getItems() {
        return AgriItemRegistry.getInstance();
    }

    @Override
    public Sounds getSounds() {
        return AgriSoundRegistry.getInstance();
    }

    @Override
    public Tabs getTabs() {
        return AgriTabs.TABS;
    }
}
