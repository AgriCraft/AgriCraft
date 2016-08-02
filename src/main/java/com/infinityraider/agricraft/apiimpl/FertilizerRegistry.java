/*
 */
package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.vanilla.BonemealWrapper;
import com.infinityraider.agricraft.api.adapter.IAgriAdapter;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizable;
import com.infinityraider.agricraft.api.adapter.IAgriAdapterRegistry;

/**
 *
 * @author RlonRyan
 */
public class FertilizerRegistry {

	private static final IAgriAdapterRegistry<IAgriFertilizer> INSTANCE = new AdapterRegistry<>();

	static {
		INSTANCE.registerAdapter(new BonemealWrapper());
	}

	public static IAgriAdapterRegistry<IAgriFertilizer> getInstance() {
		return INSTANCE;
	}

}
