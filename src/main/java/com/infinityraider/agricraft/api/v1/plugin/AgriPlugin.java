package com.infinityraider.agricraft.api.v1.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marker interface for AgriCraft plugins. All classes annotated with this annotation <em>must</em>
 * have a valid no-args constructor.
 * <p>
 * This system is loosely based off of JEI's plugin system. *
 */
@Target(ElementType.TYPE)
@Retention(value = RUNTIME)
public @interface AgriPlugin {
    /**
     * Defines the plugin's ID, usually the mod-id, the plugin will only be loaded if a mod with the given ID is present,
     * or if alwaysLoad() is set to true.
     *
     * @return the id for the plugin
     */
    String modId();

    /**
     * @return true if the plugin should always be loaded, regardless of mod id presence
     */
    boolean alwaysLoad() default false;
}
