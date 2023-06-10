package com.agricraft.agricraft.api.requirement;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.adapter.IAgriAdapter;
import com.agricraft.agricraft.api.util.IAgriRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * An interface for managing AgriCraft soils.
 */
public interface IAgriSoilRegistry extends IAgriRegistry<IAgriSoil>, IAgriAdapter<IAgriSoil> {

	/**
	 * @return the AgriCraft IAgriSoilRegistry instance
	 */
	static IAgriSoilRegistry getInstance() {
		return AgriApi.getSoilRegistry();
	}

	@Override
	default boolean accepts(@Nullable Object obj) {
		return obj instanceof BlockState && this.stream().anyMatch(soil -> soil.isVariant((BlockState) obj));
	}

	@NotNull
	@Override
	default Optional<IAgriSoil> valueOf(@Nullable Object obj) {
		if (obj instanceof ItemStack) {
			return this.valueOf(((ItemStack) obj).getItem());
		}
		if (obj instanceof BlockItem) {
			return this.valueOf(((BlockItem) obj).getBlock());
		}
		if (obj instanceof Block) {
			return this.valueOf(((Block) obj).defaultBlockState());
		}
		if (obj instanceof BlockState) {
			return this.stream().filter(soil -> soil.isVariant((BlockState) obj)).findFirst();
		}
		return Optional.empty();
	}

	void registerSoilProvider(@NotNull Block block, @NotNull IAgriSoilProvider soilProvider);

	@NotNull
	IAgriSoilProvider getProvider(@NotNull Block block);

	@NotNull
	IAgriSoil getNoSoil();

}
