package com.agricraft.agricraft.api.genetic;

import com.agricraft.agricraft.api.AgriRegistrable;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface AgriGene<T> extends AgriRegistrable {

	AgriAllele<T> defaultAllele(AgriPlant object);

	AgriAllele<T> getAllele(T value);


	void writeToNBT(CompoundTag genes, AgriAllele<T> dominant, AgriAllele<T> recessive);

	AgriGenePair<T> fromNBT(CompoundTag genes);

	void addTooltip(List<Component> tooltipComponents, T trait);

}
