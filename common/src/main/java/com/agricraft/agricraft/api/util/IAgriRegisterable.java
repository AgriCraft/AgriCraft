package com.agricraft.agricraft.api.util;

import org.jetbrains.annotations.NotNull;


public interface IAgriRegisterable<T extends IAgriRegisterable<T>> extends Comparable<T> {

	/**
	 * The unique id of the AgriRegisterable.
	 *
	 * @return the unique id of the element.
	 */
	@NotNull
	String getId();

	@Override
	default int compareTo(T other) {
		return this.getId().compareTo(other.getId());
	}

}
