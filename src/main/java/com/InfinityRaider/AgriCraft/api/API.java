package com.InfinityRaider.AgriCraft.api;

//Huge thanks to HenryLoenwind for his work and patience for this api

import com.InfinityRaider.AgriCraft.api.v0.NoAPI;

/**
 * This is the main entry point for the AgriCraft API.
 * 
 * Please DO NOT include the API in your mod jar!
 * 
 * On how to use an API without including it, see:
 * 
 * <a href=http://www.minecraftforum.net/forums/mapping-and-modding/mapping-and-modding-tutorials/1571434-tutorial-modding-with-apis> this tutorial</a>.
 * 
 * <p>
 * Example code for using it:
 * </p>
 * 
 * <pre>
 * APIBase api = API.getAPI(1);
 * if (api.getStatus().isOk() &amp;&amp; api.getVersion == 1) {
 * 	agricraft = (APIv1) api;
 * }
 * </pre>
 * 
 */
public class API {

	private static APIBase api = new NoAPI();

	/**
	 * Returns an API object. Call this with the version number of the API you
	 * compiled against. It will do its best to return you a matching object.
	 * 
	 * This will never return null, so it is important that you check the
	 * APIBase.getStatus() and APIBase.getVersion() before typecasting the
	 * result to any specific interface.
	 * 
	 * @param maxVersion
	 * @return
	 */
	public static APIBase getAPI(int maxVersion) {
		return api.getAPI(maxVersion);
	}

	/**
	 * internal use only
	 */
	public static void setAPI(APIBase api) {
		API.api = api;
	}
}
