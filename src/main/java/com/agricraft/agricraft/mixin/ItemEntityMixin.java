package com.agricraft.agricraft.mixin;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	@Shadow public abstract ItemStack getItem();

	@Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
	private void fireImmune(CallbackInfoReturnable<Boolean> cir) {
		if (AgriApi.get().getPlant(ResourceLocation.parse(AgriSeedItem.getSpecies(this.getItem()))).map(AgriPlant::isFireproof).orElse(false)) cir.setReturnValue(true);
	}
}
