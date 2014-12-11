/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture.hives;

import java.util.ArrayList;
import java.util.HashMap;

public class HiveManager {
	public static final String forest = "Forestry:forest";
	public static final String meadows = "Forestry:meadows";
	public static final String desert = "Forestry:desert";
	public static final String jungle = "Forestry:jungle";
	public static final String end = "Forestry:end";
	public static final String snow = "Forestry:snow";
	public static final String swamp = "Forestry:swamp";

	private static final HashMap<String, IHive> hives = new HashMap<String, IHive>();

	/**
	 * Get a list of all hives.
	 */
	public static ArrayList<IHive> getHives() {
		return new ArrayList<IHive>(hives.values());
	}

	/**
	 * Add new custom hives here to have them generate in the world.
	 */
	public static void put(String hiveName, IHive hive) {
		if (hives.containsKey(hiveName))
			throw new IllegalArgumentException("Hive already exists with name: " + hiveName);
		hives.put(hiveName, hive);
	}

	/**
	 * Get an existing hive to add new IHiveDrops to them.
	 */
	public static IHive get(String hiveName) {
		return hives.get(hiveName);
	}
	public static IHive getForestHive() { return get(forest); }
	public static IHive getMeadowsHive() { return get(meadows); }
	public static IHive getDesertHive() { return get(desert); }
	public static IHive getJungleHive() { return get(jungle); }
	public static IHive getEndHive() { return get(end); }
	public static IHive getSnowHive() { return get(snow); }
	public static IHive getSwampHive() { return get(swamp); }
}
