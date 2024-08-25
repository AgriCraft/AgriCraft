package com.agricraft.agricraft.common.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BucketItem.class)
public class BucketItemMixin {
    @Shadow @Final private Fluid content;

    @Definition(id = "content", field = "Lnet/minecraft/world/item/BucketItem;content:Lnet/minecraft/world/level/material/Fluid;")
    @Definition(id = "WATER", field = "Lnet/minecraft/world/level/material/Fluids;WATER:Lnet/minecraft/world/level/material/FlowingFluid;")
    @Expression("this.content == WATER")
    @ModifyExpressionValue(method = "use", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean use(boolean original) {
        return original || this.content == Fluids.LAVA;
    }

    @Definition(id = "content", field = "Lnet/minecraft/world/item/BucketItem;content:Lnet/minecraft/world/level/material/Fluid;")
    @Definition(id = "WATER", field = "Lnet/minecraft/world/level/material/Fluids;WATER:Lnet/minecraft/world/level/material/FlowingFluid;")
    @Expression("this.content == WATER")
    @ModifyExpressionValue(method = "emptyContents", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean emptyContents(boolean original) {
        return original || this.content == Fluids.LAVA;
    }
}
