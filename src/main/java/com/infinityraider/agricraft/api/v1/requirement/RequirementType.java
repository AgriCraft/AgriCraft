package com.infinityraider.agricraft.api.v1.requirement;

public enum RequirementType {
    /**
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

    /** if the presence/absence of a specific entity nearby is required */
    ENTITY,

    /** if rain / no rain is required */
    RAIN,

    /** if snow / no snow is required */
    SNOW,

    /** if the presence/absence of a specific structure nearby is required (e.g. village, nether fortress, or mod structures) */
    STRUCTURE,


    /**
     * ----------------
     * MOD REQUIREMENTS
     * ----------------
     */

    /** if a specific weather is required (for mods which add weather) */
    WEATHER,

    /** if a specific atmosphere is required (for mods which add gases / atmospheres) */
    ATMOSPHERE,

    /** if a specific temperature is required (for mods which add temperature) */
    TEMPERATURE,

    /** if a specific temperature is required (for mods which add humidity) */
    HUMIDITY,

    /** if a specific season is required (for mods which add seasons) */
    SEASON,

    /** if mana is required (for magic mods) */
    MANA,

    /** if power is required (for mods with power) */
    POWER,


    /**
     * ------------------------
     * MORE EXOTIC REQUIREMENTS
     * ------------------------
     */

    /** for any other requirements not covered by the defaults */
    OTHER;


}
