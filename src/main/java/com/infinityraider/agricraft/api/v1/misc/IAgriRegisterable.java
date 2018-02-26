/*
 */
package com.infinityraider.agricraft.api.v1.misc;

import javax.annotation.Nonnull;

/**
 *
 * @author Ryan
 */
public interface IAgriRegisterable {

    /**
     * The unique id of the AgriRegisterable.
     *
     * @return the unique id of the element.
     */
    @Nonnull
    String getId();

}
