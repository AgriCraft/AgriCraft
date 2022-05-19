package com.infinityraider.agricraft.render.fluid;

import com.infinityraider.infinitylib.render.fluid.IFluidRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AgriTankWaterRenderer implements IFluidRenderer {
    private static final AgriTankWaterRenderer INSTANCE = new AgriTankWaterRenderer();

    public static AgriTankWaterRenderer getInstance() {
        return INSTANCE;
    }

    private AgriTankWaterRenderer() {}

    @Override
    public void render(BlockAndTintGetter world, BlockPos pos, VertexConsumer builder, FluidState state) {
        // Rendering of the fluid is handled by the TileEntity renderer, no need to do anything here
    }
}
