package com.agricraft.agricraft.api.stat;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for objects that have AgriStats.
 */
public interface IAgriStatProvider {

	@NotNull
	IAgriStatsMap getStats();

}
