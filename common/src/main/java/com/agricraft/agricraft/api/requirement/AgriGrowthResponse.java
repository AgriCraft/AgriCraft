package com.agricraft.agricraft.api.requirement;

import com.agricraft.agricraft.api.crop.AgriCrop;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

/**
 * Interface representing how plants behave under certain conditions
 */
public interface AgriGrowthResponse {

	/**
	 * Check for fertility, if this returns true, the value is fulfilled and a plant can grow
	 *
	 * @return true if growing is allowed
	 */
	boolean isFertile();

	/**
	 * If this method returns true, plants will slowly revert their growth stage, and eventually wither away
	 *
	 * @return true if growing is reversed
	 */
	boolean isLethal();

	/**
	 * Some grow conditions can be so severe they kill a plant instantly, e.g. lava
	 *
	 * @return true if a plant should be killed instantly
	 */
	default boolean isInstantKill() {
		return false;
	}

	/**
	 * When a plant is killed instantly, this is called, which allows to perform certain actions,
	 * this includes spawning particles and/or playing a sound
	 *
	 * @param crop the crop where the plant was killed
	 */
	default void onPlantKilled(AgriCrop crop) {
	}

	/**
	 * The priority of the response against other response.
	 * The highest priority response will be used to determine the final response for the plant growth.
	 * The default priority is: instant kill > lethal > infertile > fertile
	 *
	 * @return the priority of the response
	 */
	default int priority() {
		if (isInstantKill()) {
			return 3;
		}
		if (isLethal()) {
			return 2;
		}
		if (!isFertile()) {
			return 1;
		}
		return 0;
	}

	/**
	 * Default implementation representing a fertile response
	 */
	AgriGrowthResponse FERTILE = new AgriGrowthResponse() {
		@Override
		public boolean isFertile() {
			return true;
		}

		@Override
		public boolean isLethal() {
			return false;
		}

		@Override
		public String toString() {
			return "AgriGrowthResponse{FERTILE}";
		}
	};

	/**
	 * Default implementation representing an infertile response
	 */
	AgriGrowthResponse INFERTILE = new AgriGrowthResponse() {
		@Override
		public boolean isFertile() {
			return false;
		}

		@Override
		public boolean isLethal() {
			return false;
		}

		@Override
		public String toString() {
			return "AgriGrowthResponse{INFERTILE}";
		}
	};

	/**
	 * Default implementation representing a lethal response
	 */
	AgriGrowthResponse LETHAL = new AgriGrowthResponse() {
		@Override
		public boolean isFertile() {
			return false;
		}

		@Override
		public boolean isLethal() {
			return true;
		}

		@Override
		public String toString() {
			return "AgriGrowthResponse{LETHAL}";
		}
	};

	/**
	 * Default implementation representing a response where lava destroys the plant
	 */
	AgriGrowthResponse KILL_IT_WITH_FIRE = new AgriGrowthResponse() {
		@Override
		public boolean isFertile() {
			return false;
		}

		@Override
		public boolean isLethal() {
			return true;
		}

		@Override
		public boolean isInstantKill() {
			return true;
		}

		@Override
		public void onPlantKilled(AgriCrop crop) {
			Level world = crop.getLevel();
			if (world instanceof ServerLevel) {
				double x = crop.getBlockPos().getX() + 0.5;
				double y = crop.getBlockPos().getY() + 0.5;
				double z = crop.getBlockPos().getZ() + 0.5;
				for (int i = 0; i < 3; ++i) {
					world.addParticle(ParticleTypes.LARGE_SMOKE,
							x + 0.25 * world.getRandom().nextDouble(), y, z + 0.25 * world.getRandom().nextDouble(),
							1, 0, 1);
				}
				world.playSound(null, x, y, z, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS,
						0.2F + world.getRandom().nextFloat() * 0.2F, 0.9F + world.getRandom().nextFloat() * 0.15F);
			}
		}

		@Override
		public String toString() {
			return "AgriGrowthResponse{KILL_IT_WITH_FIRE}";
		}
	};

}
