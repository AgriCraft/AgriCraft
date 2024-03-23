package com.agricraft.agricraft.api.adapter;

import com.agricraft.agricraft.api.fertilizer.AgriFertilizer;
import com.agricraft.agricraft.api.genetic.AgriGenome;

import java.util.ArrayList;
import java.util.List;

public class AgriAdapters {

	public static final List<AgriAdapter<AgriGenome>> GENOME_ADAPTERS = new ArrayList<>();
	public static final List<AgriAdapter<AgriFertilizer>> FERTILIZER_ADAPTERS = new ArrayList<>();

	static {
		GENOME_ADAPTERS.add(new GenomeAdapter());
		FERTILIZER_ADAPTERS.add(new FertilizerAdapter());
	}

}
