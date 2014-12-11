/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture.hives;

import forestry.api.apiculture.IHiveDrop;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.ArrayList;

/**
 * A basic hive implementation. Custom hives can subclass this.
 */
public abstract class HiveBasic implements IHive {

	protected Block hiveBlock;
	protected int hiveMeta;
	protected ArrayList<IHiveDrop> drops = new ArrayList<IHiveDrop>();
	protected float genChance;

	public HiveBasic(Block hiveBlock, int hiveMeta, float genChance) {
		if (hiveBlock == null)
			throw new IllegalArgumentException("Tried to create hive with null hive block");
		this.hiveBlock = hiveBlock;
		this.hiveMeta = hiveMeta;
		this.genChance = genChance;
	}

	@Override
	public Block getHiveBlock() {
		return hiveBlock;
	}

	@Override
	public int getHiveMeta() {
		return hiveMeta;
	}

	@Override
	public ArrayList<IHiveDrop> getDrops() {
		return drops;
	}

	@Override
	public void addDrop(IHiveDrop drop) {
		drops.add(drop);
	}

	@Override
	public float genChance() {
		return genChance;
	}

	@Override
	public void postGen(World world, int x, int y, int z) {

	}

	@Override
	public boolean isGoodBiome(BiomeGenBase biome) {
		return true;
	}

	@Override
	public boolean isGoodHumidity(EnumHumidity humidity) {
		return true;
	}

	@Override
	public boolean isGoodTemperature(EnumTemperature temperature) {
		return true;
	}

	@Override
	public boolean isGoodLocation(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean canReplace(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		Material material = block.getMaterial();
		return (material.isReplaceable() && !material.isLiquid()) ||
				material == Material.air ||
				material == Material.plants;
	}
}
