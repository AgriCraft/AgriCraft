/*
 */
package com.infinityraider.agricraft.reference;

/**
 *
 * @author RlonRyan
 */
public enum AgriNuggetType {
	Emerald("gemEmerald", "oreEmerald"), Diamond("gemDiamond", "oreDiamond"), Quartz("quartz", "quartz"), Iron, Copper, Tin, Lead, Silver, Aluminum, Nickel, Platinum, Osmium;
	public final String nugget;
	public final String ingot;
	public final String ore;

	private AgriNuggetType() {
		this.nugget = "nugget_" + this.name().toLowerCase();
		this.ingot = "ingot" + this.name();
		this.ore = "ore" + this.name();
	}

	private AgriNuggetType(String ingot, String ore) {
		this.nugget = "nugget_" + this.name().toLowerCase();
		this.ingot = ingot;
		this.ore = ore;
	}
	private static String[] nuggets;

	public static String[] getNuggets() {
		if (nuggets == null) {
			nuggets = new String[AgriNuggetType.values().length];
			for (int i = 0; i < AgriNuggetType.values().length; i++) {
				nuggets[i] = AgriNuggetType.values()[i].nugget;
			}
		}
		return nuggets;
	}
	
}
