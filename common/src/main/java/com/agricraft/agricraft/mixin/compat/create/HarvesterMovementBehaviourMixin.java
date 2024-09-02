package com.agricraft.agricraft.mixin.compat.create;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/*
 * Create does not really provide a way to implement compatibility for more complex crops.
 * Therefore, in order to obtain compatibility, we must employ a rather aggressive approach,
 * which consists of overriding their default harvesting behaviour with our own.
 * This can be toggled in the config, so if people do not like this, they can disable it.
 * Until Create exposes harvesting logic in an API or via IMC, we must do it this way, unfortunately.
 *
 * Additionally, we have to use a mixin due to mod loading order being non-deterministic on Fabric.
 */
@Mixin(value = HarvesterMovementBehaviour.class, remap = false)
public class HarvesterMovementBehaviourMixin {
    @Inject(method = "visitNewPosition", at = @At("HEAD"), cancellable = true)
    private void visitNewPosition(MovementContext context, BlockPos pos, CallbackInfo ci) {
        Level level = context.world;
        if (!level.isClientSide()) {
            Optional<AgriCrop> optional = AgriApi.getCrop(level, pos);
            if (optional.isPresent()) {
                AgriCrop crop = optional.get();
                if (crop.canBeHarvested()) {
                    crop.getHarvestProducts(itemStack -> ((MovementBehaviour) this).dropItem(context, itemStack));
                    crop.setGrowthStage(crop.getPlant().getGrowthStageAfterHarvest());
                    crop.getPlant().onHarvest(crop, null);
                    ci.cancel();
                }
            }
        }
    }
}
