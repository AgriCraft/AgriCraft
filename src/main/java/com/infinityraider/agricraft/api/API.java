package com.infinityraider.agricraft.api;

//Huge thanks to HenryLoenwind for his work and patience for this api

import com.infinityraider.agricraft.api.v0.NoAPI;

/**
 * This is the main entry point for the AgriCraft API.
 * 
 * Please DO NOT include the API in your mod jar!
 * 
 * On how to use an API without including it, see:
 * 
 * <a href=http://www.minecraftforum.net/forums/mapping-and-modding/mapping-and-modding-tutorials/1571434-tutorial-modding-with-apis> this tutorial</a>.
 *
 *
 * <p>
 * Example code for using the API can be found in the example package, you don't have to do it this way, but if you have no idea how to use it without shipping the api in your jar, it might help.
 * But in general, short version:
 * </p>
 * 
 * <pre>
 * APIBase api = API.getAPI(1);
 * if (api.getStatus().isOk() &amp;&amp; api.getVersion == 1) {
 * 	agricraft = (APIv1) api;
 * }
 * </pre>
 *
 *
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
	 * @param maxVersion The maximum version allowed to be returned, effectively returned version might be lower
	 * @return an APIBase object which interfaces with AgriCraft
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
