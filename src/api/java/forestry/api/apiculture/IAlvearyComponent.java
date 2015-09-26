/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import forestry.api.core.IClimateControlled;

/**
 * Needs to be implemented by TileEntities that want to be part of an alveary.
 */
public interface IAlvearyComponent {

	/**
	 * Implemented by alveary parts to apply a beeListener to the completed structure.
	 */
	interface BeeListener extends IAlvearyComponent {
		IBeeListener getBeeListener();
	}

	/**
	 * Implemented by alveary parts to apply a beeModifier to the completed structure.
	 */
	interface BeeModifier extends IAlvearyComponent {
		IBeeModifier getBeeModifier();
	}

	/**
	 * Implemented by alveary parts to apply a climate change to the completed structure.
	 */
	interface Climatiser extends IAlvearyComponent {
		/**
		 * Called every tick by the alveary.
		 * @param tickCount the number of ticks in the world
		 * @param alveary the climate controlled alveary
		 */
		void changeClimate(int tickCount, IClimateControlled alveary);
	}

	/**
	 * Implemented by alveary parts to receive ticks from the completed structure.
	 */
	interface Active extends IAlvearyComponent {
		void updateServer(int tickCount);
		void updateClient(int tickCount);
	}

}
