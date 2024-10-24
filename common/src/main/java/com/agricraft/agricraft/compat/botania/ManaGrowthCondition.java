package com.agricraft.agricraft.compat.botania;

import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.plant.IAgriPlantModifier;
import com.agricraft.agricraft.api.requirement.AgriGrowthConditionRegistry;
import com.agricraft.agricraft.api.requirement.AgriGrowthResponse;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.ManaPool;
import vazkii.botania.api.mana.ManaReceiver;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Optional;

public class ManaGrowthCondition extends AgriGrowthConditionRegistry.BaseGrowthCondition<Integer> {

	private static final HashMap<BlockPos, WeakReference<ManaPool>> POOL_CACHE = new HashMap<>();

	public ManaGrowthCondition() {
		// set null for response and getter 'cause we are doing it ourselves
		super("botania_mana", null, null);
	}

	public static ManaPool getPool(BlockPos pos, Level level) {
		WeakReference<ManaPool> ref = POOL_CACHE.get(pos);
		ManaPool pool;
		if (ref == null) {
			pool = BotaniaAPI.instance().getManaNetworkInstance().getClosestPool(pos, level, 16);
			ref = new WeakReference<>(pool);
			POOL_CACHE.put(pos, ref);
		} else {
			pool = ref.get();
			if (pool == null) {
				pool = BotaniaAPI.instance().getManaNetworkInstance().getClosestPool(pos, level, 16);
				ref = new WeakReference<>(pool);
				POOL_CACHE.put(pos, ref);
			}
		}
		return pool;
	}

	public static void removePoll(ManaReceiver pool) {
		POOL_CACHE.entrySet().removeIf(e -> e.getValue().get() == pool);
	}

	@Override
	public AgriGrowthResponse check(AgriCrop crop, Level level, BlockPos pos, int strength) {
		// kinda tricky, but we check if the plant has the mana modifier
		Optional<IAgriPlantModifier> manaModifier = crop.getPlant().getModifiers().filter(m -> m.getClass() == ManaPlantModifier.class).findFirst();
		if (manaModifier.isPresent()) {
			ManaPlantModifier modifier = (ManaPlantModifier) manaModifier.get();
			ManaPool pool = getPool(pos, level);
			if (pool != null && pool.isOutputtingPower()) {
				if (pool.getCurrentMana() >= modifier.cost()) {
					return AgriGrowthResponse.FERTILE;
				}
			}
		}
		return AgriGrowthResponse.INFERTILE;
	}

	@Override
	public AgriGrowthResponse apply(AgriPlant plant, int strength, Integer value) {
		// we're not displaying the condition on the journal nor jei nor emi, so we can return any value we want.
		return AgriGrowthResponse.FERTILE;
	}

}
