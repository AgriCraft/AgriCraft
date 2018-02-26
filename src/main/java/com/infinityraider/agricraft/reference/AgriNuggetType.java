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
    Quartz("gemQuartz", "oreQuartz"),
    Iron,
    Copper,
    Tin,
    Lead,
    Silver,
    Aluminum,
    Nickel,
    Platinum,
    Osmium,
    Unknown;

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

    /**
     * Retrieves the nugget type corresponding to a meta. This method is index-safe, and will return
     * the unknown type for bad indices.
     *
     * @param i the meta of the nugget.
     * @return the nugget type corresponding to the given index.
     */
    public static AgriNuggetType getNugget(int i) {
        if (i >= 0 && i < values().length) {
            return values()[i % values().length];
        } else {
            return Unknown;
        }
    }

}
