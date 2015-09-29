/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import java.util.Collection;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;

public interface IFlowerRegistry {

	Set<IFlower> getAcceptableFlowers(String flowerType);
	
	boolean growFlower(String flowerType, World world, IIndividual individual, int x, int y, int z);

	/**
	 * @return the coordinates of a nearby accepted flower or null if there is none.
	 */
	ChunkCoordinates getAcceptedFlowerCoordinates(IBeeHousing beeHousing, IBee bee, String flowerType);

	boolean isAcceptedFlower(String flowerType, World world, int x, int y, int z);
	
	/**
	 * Registers a non-plantable flower, but bees accept them.
	 *
	 * @param flowerTypes See {@link forestry.api.apiculture.FlowerManager}.FlowerTypeXXX
	 */
	void registerAcceptableFlower(Block flowerBlock, String... flowerTypes);
	void registerAcceptableFlower(Block flowerBlock, int flowerMeta, String... flowerTypes);
	
	void registerGrowthRule(IFlowerGrowthRule rule, String... flowerTypes);
	
	/**
	 * Registers a plantable flower.
	 * The distribution is based on its own weight and the total number of plants for this flowerType.
	 * 
	 * @param weight Weight for the Flower (Vanilla = 1.0, Modded flowers < 1.0)
	 * @param flowerTypes See {@link forestry.api.apiculture.FlowerManager}.FlowerTypeXXX
	 */
	void registerPlantableFlower(Block flowerBlock, int flowerMeta, double weight, String... flowerTypes);

	IFlower getRandomPlantableFlower(String flowerType, Random rand);

	/** Returns all known flower types. */
	Collection<String> getFlowerTypes();
}
