package com.InfinityRaider.AgriCraft.items;

public class ItemNugget extends ItemAgricraft {
    private final String name;

    public ItemNugget(String name) {
        super("nugget"+name);
        this.name = "nugget"+name;
    }

    @Override
    protected String getInternalName() {
        return name;
    }
}
