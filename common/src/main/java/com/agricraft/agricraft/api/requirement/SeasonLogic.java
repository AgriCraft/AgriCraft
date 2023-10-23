package com.agricraft.agricraft.api.requirement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.function.BiFunction;

public class SeasonLogic {

	public static final SeasonLogic INSTANCE = new SeasonLogic();
	private Object owner;
	private BiFunction<Level, BlockPos, AgriSeason> getter;

	private SeasonLogic() {
		this.owner = null;
		this.getter = (world, pos) -> AgriSeason.ANY;
	}


	public boolean isActive() {
		return this.owner != null;
	}

	public AgriSeason getSeason(Level world, BlockPos pos) {
//		if(this.isGreenHouse(world, pos)) {
//			return AgriSeason.ANY;
//		}
		return this.getter.apply(world, pos);
	}

	public Object getOwner() {
		return this.owner;
	}

	public void claim(Object owner, BiFunction<Level, BlockPos, AgriSeason> getter) {
		this.owner = owner;
		this.getter = getter;
	}

}
