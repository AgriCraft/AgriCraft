package com.infinityraider.agricraft.api.v1.fertilizer;

import com.infinityraider.agricraft.api.v1.registry.IAgriAdapter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IAgriFertilizer extends IAgriAdapter<IAgriFertilizer> {

	/**
	 * return true if this fertilizer is allowed to speed up growth of a crop of
	 * this tier
	 */
	boolean isFertilizerAllowed(int tier);

	/**
	 * wether or not this mod can be used on a cross crop to trigger a mutation
	 * (does not override configuration option)
	 */
	boolean canTriggerMutation();

	/**
	 * this is called when the fertilizer is used on a crop, this only is called
	 * if true is returned from hasSpecialBehaviour
	 */
	boolean applyFertilizer(EntityPlayer player, World world, BlockPos pos, IAgriFertilizable target, ItemStack stack, Random random);

	/**
	 * this is called on the client when the fertilizer is applied, can be used
	 * for particles or other visual effects
	 */
	@SideOnly(Side.CLIENT)
	void performClientAnimations(int meta, World world, BlockPos pos);

	@Override
	default boolean accepts(NBTTagCompound tag) {
		return false;
	}

	@Override
	default IAgriFertilizer getValue(ItemStack stack) {
		return this.accepts(stack) ? this : null;
	}

	@Override
	default IAgriFertilizer getValue(NBTTagCompound tag) {
		return this.accepts(tag) ? this : null;
	}

}
