/*
 */
package com.infinityraider.agricraft.apiimpl.v1;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapter;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizable;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterRegistry;

/**
 *
 * @author RlonRyan
 */
public class FertilizerRegistry {
	
	public static IAgriAdapterRegistry<IAgriFertilizer> getInstance() {
		return APIimplv1.getInstance().getFertilizerRegistry();
	}
	
	public static class BonemealWrapper implements IAgriFertilizer, IAgriAdapter<IAgriFertilizer> {
		
		private static final ItemStack BONEMEAL = new ItemStack(Items.DYE, 1, 15);

		@Override
		public boolean isFertilizerAllowed(int tier) {
			// TODO: CORRECT THIS!
			return tier < 3;
		}

		@Override
		public boolean canTriggerMutation() {
			return false;
		}

		@Override
		public boolean applyFertilizer(EntityPlayer player, World world, BlockPos pos, IAgriFertilizable target, ItemStack stack, Random random) {
			if (target.acceptsFertilizer(this) && target.onApplyFertilizer(this, random)) {
				if (player == null || !player.capabilities.isCreativeMode) {
					stack.stackSize = stack.stackSize - 1;
				}
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void performClientAnimations(int meta, World world, BlockPos pos) {
			// TODO!
		}
		
		@Override
		public boolean accepts(Object obj) {
			return obj instanceof ItemStack && BONEMEAL.isItemEqual((ItemStack)obj);
		}

		@Override
		public IAgriFertilizer getValue(Object obj) {
			if (obj instanceof ItemStack && BONEMEAL.isItemEqual((ItemStack)obj)) {
				return this;
			} else {
				return null;
			}
		}

	}

}
