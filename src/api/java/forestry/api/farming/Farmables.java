/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package api.forestry.api.farming;

import forestry.api.farming.IFarmInterface;

import java.util.Collection;
import java.util.HashMap;

public class Farmables {
	/**
	 * Can be used to add IFarmables to some of the vanilla farm logics.
	 * 
	 * Identifiers: farmArboreal farmWheat farmGourd farmInfernal farmPoales farmSucculentes farmVegetables farmShroom
	 */
	public static HashMap<String, Collection<forestry.api.farming.IFarmable>> farmables = new HashMap<String, Collection<forestry.api.farming.IFarmable>>();

	public static IFarmInterface farmInterface;
}
