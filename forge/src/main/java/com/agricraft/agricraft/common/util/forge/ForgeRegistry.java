package com.agricraft.agricraft.common.util.forge;

import com.agricraft.agricraft.common.util.PlatformRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ForgeRegistry<T> implements PlatformRegistry<T> {

	private final DeferredRegister<T> registry;

	public ForgeRegistry(Registry<T> registry, String modid) {
		this.registry = DeferredRegister.create(registry.key(), modid);
	}

	@Override
	public <I extends T> Entry<I> register(String id, Supplier<I> supplier) {
		RegistryObject<I> object = this.registry.register(id, supplier);
		return new ForgeRegistryEntry<>(object);
	}

	@Override
	public void init() {
		this.registry.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	public static class ForgeRegistryEntry<T> implements Entry<T> {

		private final RegistryObject<T> object;

		public ForgeRegistryEntry(RegistryObject<T> object) {
			this.object = object;
		}

		@Override
		public ResourceLocation id() {
			return this.object.getId();
		}

		@Override
		public T get() {
			return this.object.get();
		}

	}

}
