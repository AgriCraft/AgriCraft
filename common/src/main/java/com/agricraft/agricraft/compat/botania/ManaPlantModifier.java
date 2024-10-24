package com.agricraft.agricraft.compat.botania;

import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.plant.IAgriPlantModifier;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.ManaPool;

public record ManaPlantModifier(int cost) implements IAgriPlantModifier {

	public static final String ID = "agricraft:botania_mana";

	@Override
	public void onGrowth(AgriCrop crop) {
		ManaPool pool = ManaGrowthCondition.getPool(crop.getBlockPos(), crop.getLevel());
		if (pool.isOutputtingPower()) {
			System.out.println("mana was: " + pool.getCurrentMana());
			pool.receiveMana(-cost);
			System.out.println("mana is now: " + pool.getCurrentMana());
		}
		BotaniaAPI.instance().sparkleFX(crop.getLevel(),
				crop.getBlockPos().getX() + 0.5 + 0.5 * Math.random(),
				crop.getBlockPos().getY() + 0.5 + 0.5 * Math.random(),
				crop.getBlockPos().getZ() + 0.5 + 0.5 * Math.random(),
				67.0F / 255.0F,
				180.0F / 255.0F,
				1.0F,
				(float) Math.random(),
				5
		);
	}

}
