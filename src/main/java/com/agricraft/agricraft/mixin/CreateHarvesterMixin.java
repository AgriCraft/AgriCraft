package com.agricraft.agricraft.mixin;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * Create's mechanical harvester only cuts vanilla crop blocks and blocks with an age property, so it skips
 * AgriCraft crops, which keep their growth in the block entity. Harvest a mature plant the same way a right
 * click does (products into the contraption, plant back to its after-harvest stage), and leave every other
 * AgriCraft crop alone so the harvester never breaks crop sticks. Does nothing without Create.
 */
@Pseudo
@Mixin(HarvesterMovementBehaviour.class)
public class CreateHarvesterMixin {

	@Inject(method = "visitNewPosition", at = @At("HEAD"), cancellable = true, require = 0)
	private void agricraft$harvestCrop(MovementContext context, BlockPos pos, CallbackInfo ci) {
		Level level = context.world;
		if (level.isClientSide()) {
			return;
		}
		Optional<AgriCrop> optional = AgriApi.get().getCrop(level, pos);
		if (optional.isEmpty()) {
			return;
		}
		ci.cancel();
		AgriCrop crop = optional.get();
		if (crop.isCrossCropSticks() || !crop.hasPlant() || !crop.canBeHarvested()) {
			return;
		}
		MovementBehaviour harvester = (MovementBehaviour) this;
		crop.getHarvestProducts(stack -> harvester.collectOrDropItem(context, stack));
		crop.setGrowthStage(crop.getPlant().getGrowthStageAfterHarvest());
		crop.getPlant().onHarvest(crop, null);
	}
}
