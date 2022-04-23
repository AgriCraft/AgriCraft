package com.infinityraider.agricraft.api.v1.requirement;

import net.minecraftforge.common.IExtensibleEnum;

public enum RequirementType implements IExtensibleEnum {
    /*
     * --------------------
     * VANILLA REQUIREMENTS
     * --------------------
     */

    /** if a specific soil is required */
    SOIL,

    /** if a specific light value / range is required */
    LIGHT,

    /** if a specific redstone signal strength is required */
    REDSTONE,

    /** if a specific liquid is required */
    LIQUID,

    /** if a specific biome is required */
    BIOME,

    /** if a specific climate is required */
    CLIMATE,

    /** if a specific dimension is required */
    DIMENSION,

    /** if the presence/absence of specific weeds is required */
    WEEDS,

    /** if a specific time of the day is required */
    TIME,

    /** if the presence/absence of a specific block below is required */
    BLOCK_BELOW,

    /** if the presence/absence of a certain amount of specific blocks is required nearby */
    BLOCKS_NEARBY,

    /** if the presence/absence of a certain amount of specific tile entities is required nearby */
    TILES_NEARBY,

    /** if the presence/absence of a specific entity nearby is required */
    ENTITY,

    /** if rain / no rain is required */
    RAIN,

    /** if snow / no snow is required */
    SNOW,

    /** if the presence/absence of a specific structure nearby is required (e.g. village, nether fortress, or mod structures) */
    STRUCTURE,


    /*
     * ----------------
     * MOD REQUIREMENTS
     * ----------------
     */

    /** if a specific weather is required (for mods which add weather) */
    WEATHER,

    /** if a specific atmosphere is required (for mods which add gases / atmospheres) */
    ATMOSPHERE,

    /** if there is too much/little radiation (for mods which add radioactivity) */
    RADIATION,

    /** if a specific temperature is required (for mods which add temperature) */
    TEMPERATURE,

    /** if a specific temperature is required (for mods which add humidity) */
    HUMIDITY,

    /** if a specific season is required (for mods which add seasons) */
    SEASON,

    /** if mana is required (for magic mods) */
    MANA,

    /** if there is too much/little pollution (for pollution adding mods) */
    POLLUTION,

    /** if power is required (for mods with power) */
    POWER,


    /*
     * ------------------------
     * MORE EXOTIC REQUIREMENTS
     * ------------------------
     */

    /** for any other requirements not covered by the defaults */
    OTHER;


    /**
     * Method to create custom requirement types, if it would be needed
     * @param name the name
     * @return the new RequirementType
     */
    public static RequirementType create(String name) {
        throw new IllegalStateException("Enum not extended");
    }
}
