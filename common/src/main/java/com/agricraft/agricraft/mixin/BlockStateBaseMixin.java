package com.agricraft.agricraft.mixin;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Shadow
    @Final
    @Mutable
    private int lightEmission;

    @Shadow
    public abstract FluidState getFluidState();

    @Unique
    boolean agricraft$setFluidLightEmission = false;

    @Inject(method = "getLightEmission", at = @At("HEAD"))
    private void getLightEmission(CallbackInfoReturnable<Integer> cir) {
        if (!this.agricraft$setFluidLightEmission) {
            this.agricraft$setFluidLightEmission = true;

            final FluidState fluidState = getFluidState();

            if (fluidState.getType() != Fluids.EMPTY) {
                final BlockState fluidBlockState = fluidState.createLegacyBlock();

                if (fluidBlockState != (BlockState) (Object) this) {
                    final int fluidLightEmission = fluidBlockState.getLightEmission();

                    if (fluidLightEmission > this.lightEmission) {
                        this.lightEmission = fluidLightEmission;
                    }
                }
            }
        }
    }
}
