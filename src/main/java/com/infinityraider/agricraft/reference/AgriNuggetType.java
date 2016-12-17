/*
 */
package com.infinityraider.agricraft.reference;

/**
 *
 *
 */
public enum AgriNuggetType {

    Emerald("gemEmerald", "oreEmerald"),
    Diamond("gemDiamond", "oreDiamond"),
    Quartz("quartz", "quartz"),
    Iron,
    Copper,
    Tin,
    Lead,
    Silver,
    Aluminum,
    Nickel,
    Platinum,
    Osmium;

    public final String texture;
    public final String nugget;
    public final String ingot;
    public final String ore;

    private AgriNuggetType() {
        this.nugget = "nugget" + this.name();
        this.ingot = "ingot" + this.name();
        this.ore = "ore" + this.name();
        this.texture = "agricraft:items/nugget_" + this.name().toLowerCase();
    }

    private AgriNuggetType(String ingot, String ore) {
        this.nugget = "nugget" + this.name();
        this.ingot = ingot;
        this.ore = ore;
        this.texture = "agricraft:items/nugget_" + this.name().toLowerCase();
    }
    
    public String getUnlocalizedName() {
        return "item.agricraft:nugget_" + this.name().toLowerCase();
    }

    public static AgriNuggetType getNugget(int i) {
        return values()[i % values().length];
    }

}
