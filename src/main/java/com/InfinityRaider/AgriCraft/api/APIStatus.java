package com.InfinityRaider.AgriCraft.api;

/**
 * Descriptive status of an API object.
 *
 */
public enum APIStatus {
	/**
	 * The API was not properly initialized. Possible reasons:
	 * 
	 * - You called getAPI() before AgriCraft was initialized. Don't call
	 * getAPI() in the PREINIT phase.
	 * 
	 * - Someone included the API with their mod, AgriCraft is not actually
	 * installed.
	 */
	API_NOT_INITIALIZED,
	/**
	 * The API was properly loaded and your API object is ready to go.
	 * 
	 * You still need to check the version of the returned API object, it may be
	 * an older version than you expect.
	 * 
	 * Please note that during the init phases not all methods may return final
	 * values.
	 */
	OK,
	/**
	 * The API was properly loaded and your API object is ready to go.
	 * 
	 * The AgriCraft that returned your API object supports a newer version than
	 * the one you requested. You got an API object of the requested version and
	 * it is fully functional.
	 */
	BACKLEVEL_OK,
	/**
	 * The API was properly loaded and your API object is ready to go.
	 * 
	 * The AgriCraft that returned your API object supports a newer version than
	 * the one you requested. You got an API object of the requested version and
	 * it is basically functional. Some functionality may be missing.
	 */
	BACKLEVEL_LIMITED,
	/**
	 * No API object for the API version you requested can be supplied.
	 * 
	 * The AgriCraft that returned your API object supports a newer version than
	 * the one you requested. You got a non-functional API object because the
	 * requested version is no longer supported.
	 */
	BACKLEVEL_UNSUPPORTED,
	/**
	 * An error occurred and no API object could be provided.
	 * 
	 * You sill got a non-functional API object.
	 */
	ERROR;

	/**
	 * Shortcut to check for one of the 3 "OK" states that allow you to work
	 * with the API object.
	 */
	public boolean isOK() {
		return this == OK || this == BACKLEVEL_OK || this == BACKLEVEL_LIMITED;
	}
}
