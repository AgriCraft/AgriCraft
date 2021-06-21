package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.IAgriContent;

public final class AgriContent implements IAgriContent {
    private static final AgriContent INSTANCE = new AgriContent();

    public static AgriContent getInstance() {
        return INSTANCE;
    }

    private AgriContent() {}

    @Override
    public Blocks getBlocks() {
        return AgriCraft.instance.getModBlockRegistry();
    }

    @Override
    public Enchantments getEnchantments() {
        return AgriCraft.instance.getModEnchantmentRegistry();
    }

    @Override
    public Fluids getFluids() {
        return AgriCraft.instance.getModFluidRegistry();
    }

    @Override
    public Items getItems() {
        return AgriCraft.instance.getModItemRegistry();
    }

    @Override
    public Sounds getSounds() {
        return AgriCraft.instance.getModSoundRegistry();
    }

    @Override
    public Tabs getTabs() {
        return AgriTabs.TABS;
    }
}
