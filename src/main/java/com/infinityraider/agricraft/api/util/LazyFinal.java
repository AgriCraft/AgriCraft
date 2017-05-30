/*
 */
package com.infinityraider.agricraft.api.util;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Class for final fields that should be lazily populated.
 *
 * @param <T> The type of the field.
 */
public final class LazyFinal<T> {

    private T value;
    private Supplier<T> populator;

    /**
     * Constructs a lazy final using the given populator function.
     * <p>
     * Notice that this class makes absolutely no guarantee as to when, or even
     * if the populator is called to populate the field. However, this class
     * does guarantee that the populator is called no more than exactly once,
     * and then is nulled out immediately afterwards.
     *
     * @param populator The <em>non-null</em> populator to be used to populate the field, when it
     * is determined that the field should be populated.
     */
    public LazyFinal(Supplier<T> populator) {
        this.populator = Objects.requireNonNull(populator);
    }

    /**
     * Determines if the field has been populated yet.
     * <p>
     * Notice, this method does is not synchronized, and does not block, to the
     * point that this method may return false even when another thread is in
     * the process of populating the field. As such, this is more of an
     * informative convince method than anything else, and should not be used in
     * mission-critical logic.
     *
     * @return {@literal true} if (in the context of this thread) the internal
     * value field has yet to be set, {@code false} otherwise.
     */
    public boolean isSet() {
        return (this.populator == null);
    }

    /**
     * Forces the field to be populated, if it has not been already.
     * <p>
     * This method <em>does not</em> guarantee that the populator will be
     * invoked, but does guarantee that the value will be set following its
     * invocation.
     * <p>
     * Notice, this method is synchronized, and therefore blocking. Therefore,
     * one should avoid calling this method erratically, as it may cause the
     * thread to wait. Arguably, this method should never be called externally,
     * as a lazy field should only be populated at the last minute possible.
     *
     * @see #get()
     */
    public synchronized void set() {
        if (this.populator != null) {
            this.value = populator.get();
            this.populator = null;
        }
    }

    /**
     * Fetches the value of the field, populating the field if it has not yet
     * been populated.
     * <p>
     * Notice, that this method uses {@link set()} internally to set the field,
     * if when tested the field does not appear to have been set. In this
     * manner, this method may block for the brief duration of the set
     * operation. However, once the field has been set - and the change
     * propagated across all threads - then (and only then) the method is
     * guaranteed to no longer block.
     *
     * @see #set()
     *
     * @return
     */
    public T get() {
        if (this.populator != null) {
            this.set();
        }
        return this.value;
    }

}
