package com.infinityraider.agricraft.api.v1.util;

import javax.annotation.Nonnull;

public interface IAgriRegisterable<T extends IAgriRegisterable<T>> extends Comparable<T> {

    /**
     * The unique id of the AgriRegisterable.
     *
     * @return the unique id of the element.
     */
    @Nonnull
    String getId();

    @Override
    default int compareTo(T other) {
        return this.getId().compareTo(other.getId());
    }
}
