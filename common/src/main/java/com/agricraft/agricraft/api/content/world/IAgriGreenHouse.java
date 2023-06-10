package com.agricraft.agricraft.api.content.world;

import com.agricraft.agricraft.api.AgriApi;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * Interface representing a greenhouse structure in the world
 *
 * A greenhouse consists of an arbitrary, enclosed volume in the world,
 * if that volume is too large, or has insufficient glass, it will not be a valid greenhouse
 */
public interface IAgriGreenHouse {

	/**
	 * Fetches an existing greenhouse from the world
	 *
	 * @param world world
	 * @param pos   position
	 * @return optional containing a greenhouse for the position, or empty if there is none
	 */
	static Optional<IAgriGreenHouse> get(Level world, BlockPos pos) {
		return AgriApi.getGreenHouse(world, pos);
	}

	/**
	 * Attempts to create a new greenhouse at the given position
	 *
	 * @param world world
	 * @param pos   position
	 * @return an optional containing the greenhouse, or empty if it was not possible to create one
	 */
	static Optional<IAgriGreenHouse> create(Level world, BlockPos pos) {
		return AgriApi.createGreenHouse(world, pos);
	}

	/**
	 * Gets the minimum BlockPos which encompasses the greenhouse
	 * note that not all blocks in the range from min to max are necessarily part of, or inside the greenhouse
	 *
	 * @return minimum BlockPos of a box enveloping the greenhouse
	 */
	BlockPos getMin();

	/**
	 * Gets the maximum BlockPos which encompasses the greenhouse
	 * note that not all blocks in the range from min to max are necessarily part of, or inside the greenhouse
	 *
	 * @return maximum BlockPos of a box enveloping the greenhouse
	 */
	BlockPos getMax();

	/**
	 * Checks if a BlockPos is part of the greenhouse (interior or wall)
	 * Note that if the greenhouse is removed, this method will always return false
	 *
	 * @param world the world
	 * @param pos   the position
	 * @return true if the position is part of the greenhouse structure
	 */
	boolean isPartOf(Level world, BlockPos pos);

	/**
	 * Checks if a BlockPos is inside of a greenhouse (interior only, walls excluded)
	 * Note that if the greenhouse is not complete, this method will always return false
	 *
	 * @param world the world
	 * @param pos   the position
	 * @return true if the position is inside the greenhouse structure
	 */
	boolean isInside(Level world, BlockPos pos);

	/**
	 * @return the current state of the greenhouse
	 */
	State getState();

	/**
	 * Quick check for completeness
	 *
	 * @return true if the state of this greenhouse is complete
	 */
	default boolean isComplete() {
		return this.getState().isComplete();
	}

	/**
	 * Quick check for insufficient glass
	 *
	 * @return true if the state of this greenhouse has insufficient glass
	 */
	default boolean hasInsufficientGlass() {
		return this.getState().hasInsufficientGlass();
	}

	/**
	 * Quick check for gaps
	 *
	 * @return true if the state of this greenhouse is gaps
	 */
	default boolean hasGaps() {
		return this.getState().hasGaps();
	}

	/**
	 * Quick check for removed
	 *
	 * @return true if the state of this greenhouse is removed
	 */
	default boolean isRemoved() {
		return this.getState().isRemoved();
	}

	/**
	 * Removes the greenhouse from the world
	 *
	 * @param world
	 */
	void remove(Level world);

	/**
	 * The state of a greenhouse, only greenhouses with a state of complete are functional
	 */
	enum State implements StringRepresentable {
		/**
		 * Complete greenhouse, fully operational
		 */
		COMPLETE(0.0F, 1.0F, 0.0F),

		/**
		 * Complete greenhouse, but insufficient glass
		 */
		INSUFFICIENT_GLASS(0.0F, 1.0F, 1.0F),

		/**
		 * The greenhouse has some gaps in its walls
		 */
		GAPS(1.0F, 1.0F, 0.0F),

		/**
		 * The greenhouse has been removed from the world
		 */
		REMOVED(1.0F, 0.0F, 0.0F);

		private final float red;
		private final float green;
		private final float blue;

		State(float red, float green, float blue) {
			this.red = red;
			this.green = green;
			this.blue = blue;
		}

		/**
		 * @return true if this is the complete state
		 */
		public boolean isComplete() {
			return this == COMPLETE;
		}

		/**
		 * @return true if this is the insufficient glass state
		 */
		public boolean hasInsufficientGlass() {
			return this == INSUFFICIENT_GLASS;
		}

		/**
		 * @return true if this is the gaps state
		 */
		public boolean hasGaps() {
			return this == GAPS;
		}

		/**
		 * @return true if this is the removed state
		 */
		public boolean isRemoved() {
			return this == REMOVED;
		}

		/**
		 * @return red tint for rendering
		 */
		public float getRed() {
			return this.red;
		}

		/**
		 * @return green tint for rendering
		 */
		public float getGreen() {
			return this.green;
		}

		/**
		 * @return blue tint for rendering
		 */
		public float getBlue() {
			return this.blue;
		}

		/**
		 * @return serialized name for use in Block Properties
		 */
		@Override
		public String getSerializedName() {
			return this.name().toLowerCase();
		}
	}

}
