/*
 */
package com.infinityraider.agricraft.api.v1.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marker interface for AgriCraft plugins. All classes annotated with this annotation <em>must</em>
 * have a valid no-args constructor.
 * <p>
 * This system is loosely based off of JEI's plugin system.
 *
 */
@Target(ElementType.TYPE)
public @interface AgriPlugin {
    /* Simple marker annotation, so no actual code here. */
}
