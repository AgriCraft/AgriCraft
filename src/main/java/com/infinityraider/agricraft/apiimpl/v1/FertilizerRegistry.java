/*
 */
package com.infinityraider.agricraft.apiimpl.v1;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.util.ItemWithMeta;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizable;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizerRegistry;

/**
 *
 * @author RlonRyan
 */
public class FertilizerRegistry implements IAgriFertilizerRegistry {

	private final Map<ItemWithMeta, IAgriFertilizer> fertilizers;

	public FertilizerRegistry() {
		this.fertilizers = new HashMap<>();
		this.fertilizers.put(new ItemWithMeta(Items.DYE, 15), new BonemealWrapper());
	}
	
	public static IAgriFertilizerRegistry getInstance() {
		return APIimplv1.getInstance().getFertilizerRegistry();
	}

	@Override
	public boolean isFertilizer(ItemStack stack) {
		return fertilizers.containsKey(new ItemWithMeta(stack));
	}

	@Override
	public IAgriFertilizer getFertilizer(ItemStack stack) {
		return fertilizers.get(new ItemWithMeta(stack));
	}

	@Override
	public boolean registerFertilizer(ItemStack stack, IAgriFertilizer fertilizer) {
		if (stack != null && fertilizer != null) {
			fertilizers.put(new ItemWithMeta(stack), fertilizer);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean unregisterFertilizer(ItemStack stack) {
		return stack != null && fertilizers.remove(new ItemWithMeta(stack)) != null;
	}

	private static class BonemealWrapper implements IAgriFertilizer {

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

	}

}
