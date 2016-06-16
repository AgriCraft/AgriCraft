/*
 */
package com.infinityraider.agricraft.utility;

import com.agricraft.agricore.core.AgriCore;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A class to aid in the execution of reflection magic.
 *
 * @author RlonRyan
 */
public class ReflectionHelper {

	// Dummy Constructor.
	private ReflectionHelper() {
	}
	
	public static <T> void forEachIn(@Nonnull Class list, @Nonnull Class<T> clazz, @Nonnull Consumer<T> consumer) {
		forEachIn(list, clazz, null, consumer);
	}

	public static <T> void forEachIn(@Nonnull Class list, @Nonnull Class<T> clazz, @Nullable T instance, @Nonnull Consumer<T> consumer) {
		for (Field f : list.getDeclaredFields()) {
			if (instance != null || Modifier.isStatic(f.getModifiers())) {
				try {
					Object obj = f.get(instance);
					if (obj != null && clazz.isAssignableFrom(obj.getClass())) {
						consumer.accept(clazz.cast(obj));
					}
				} catch (IllegalAccessException e) {
					// Oh well...
					AgriCore.getLogger("AgriCraft").warn(
							"ReflectionHelper.forEachIn() Skipping Field: \"{0}\" in Class: \"{1}\"!",
							f.getName(),
							list.getCanonicalName()
					);
				}
			}
		}
	}

}
