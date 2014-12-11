/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.util.IIcon;
import net.minecraft.world.biome.BiomeGenBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraftforge.common.BiomeDictionary;

/**
 *  Many things Forestry use temperature and humidity of a biome to determine whether they can or how they can work or spawn at a given location.
 * 
 *  This enum concerns temperature.
 */
public enum EnumTemperature {
	NONE("None", "habitats/ocean"), ICY("Icy", "habitats/snow"), COLD("Cold", "habitats/taiga"),
	NORMAL("Normal", "habitats/plains"), WARM("Warm", "habitats/jungle"), HOT("Hot", "habitats/desert"), HELLISH("Hellish", "habitats/nether");

	public final String name;
	public final String iconIndex;

	private EnumTemperature(String name, String iconIndex) {
		this.name = name;
		this.iconIndex = iconIndex;
	}

	public String getName() {
		return this.name;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return ForestryAPI.textureManager.getDefault(iconIndex);
	}

	/**
	 * Determines if a given BiomeGenBase is of HELLISH temperature, since it is treated seperatly from actual temperature values.
	 * Uses the BiomeDictionary.
	 * @param biomeGen BiomeGenBase of the biome in question
	 * @return true, if the BiomeGenBase is a Nether-type biome; false otherwise.
	 */
	public static boolean isBiomeHellish(BiomeGenBase biomeGen) {
		return BiomeDictionary.isBiomeOfType(biomeGen, BiomeDictionary.Type.NETHER);
	}

	/**
	 * Determines if a given biomeID is of HELLISH temperature, since it is treated seperatly from actual temperature values.
	 * Uses the BiomeDictionary.
	 * @param biomeID ID of the BiomeGenBase in question
	 * @return true, if the biomeID is a Nether-type biome; false otherwise.
	 */
	public static boolean isBiomeHellish(int biomeID) {
		return BiomeDictionary.isBiomeRegistered(biomeID) && BiomeDictionary.isBiomeOfType(BiomeGenBase.getBiome(biomeID), BiomeDictionary.Type.NETHER);
	}

	/**
	 * Determines the EnumTemperature given a floating point representation of
	 * Minecraft temperature. Hellish biomes are handled based on their biome
	 * type - check isBiomeHellish.
	 * @param rawTemp raw temperature value
	 * @return EnumTemperature corresponding to value of rawTemp
	 */
	public static EnumTemperature getFromValue(float rawTemp) {
		EnumTemperature value = EnumTemperature.ICY;

		if (rawTemp > 1.00f) {
			value = EnumTemperature.HOT;
		}
		else if (rawTemp > 0.80f) {
			value = EnumTemperature.WARM;
		}
		else if (rawTemp > 0.30f) {
			value = EnumTemperature.NORMAL;
		}
		else if (rawTemp > 0.0f) {
			value = EnumTemperature.COLD;
		}

		return value;
	}

}
