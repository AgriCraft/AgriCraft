package com.agricraft.agricraft.common.util.fabric;

import com.agricraft.agricraft.common.util.PlatformRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class FabricRegistry<T> implements PlatformRegistry<T> {

	private final Registry<T> registry;
	private final String modid;

	public FabricRegistry(Registry<T> registry, String modid) {
		this.registry = registry;
		this.modid = modid;
	}

	@Override
	public <I extends T> Entry<I> register(String id, Supplier<I> supplier) {
		ResourceLocation rl = new ResourceLocation(modid, id);
		return new FabricRegistryEntry<>(rl, Registry.register(this.registry, rl, supplier.get()));
	}

	@Override
	public void init() {

	}

	public static class FabricRegistryEntry<T> implements PlatformRegistry.Entry<T> {

		private final ResourceLocation id;
		private final T value;

		public FabricRegistryEntry(ResourceLocation id, T value) {
			this.id = id;
			this.value = value;
		}

		@Override
		public ResourceLocation id() {
			return this.id;
		}

		@Override
		public T get() {
			return this.value;
		}

	}

}
