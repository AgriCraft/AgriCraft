/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

/**
 * Needs to be implemented by TileEntities that want to be part of a farm.
 */
public interface IFarmComponent {

	/**
	 * Implemented by farm parts to apply a farmListener to the completed structure.
	 */
	interface Listener extends IFarmComponent {
		IFarmListener getFarmListener();
	}

	/**
	 * Implemented by farm parts to receive ticks from the completed structure.
	 */
	interface Active extends IFarmComponent {
		void updateServer(int tickCount);
		void updateClient(int tickCount);
	}
}
