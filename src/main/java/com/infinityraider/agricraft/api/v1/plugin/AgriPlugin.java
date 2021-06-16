package com.infinityraider.agricraft.api.v1.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marker interface for AgriCraft plugins. All classes annotated with this annotation <em>must</em>
 * have a valid no-args constructor.
 * <p>
 * This system is loosely based off of JEI's plugin system.
 *
 */
@Target(ElementType.TYPE)
@Retention(value = RUNTIME)
public @interface AgriPlugin {
    String modId();

    boolean alwaysLoad() default false;
}
