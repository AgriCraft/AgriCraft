/*
 * Holder.
 */
package com.infinityraider.agricraft.models;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelResourceLocation;

/**
 *
 * @author RlonRyan
 */
public class StateUnmapper extends StateMapperBase {

	public final ModelResourceLocation model;
	
	public StateUnmapper(String model) {
		this.model = new ModelResourceLocation(model);
	}

	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		return this.model;
	}
	
}
