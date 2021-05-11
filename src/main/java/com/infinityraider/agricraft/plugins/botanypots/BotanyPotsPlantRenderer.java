package com.infinityraider.agricraft.plugins.botanypots;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BotanyPotsPlantRenderer implements IRenderUtilities {
    private static final BotanyPotsPlantRenderer INSTANCE = new BotanyPotsPlantRenderer();

    public static BotanyPotsPlantRenderer getInstance() {
        return INSTANCE;
    }

    private BotanyPotsPlantRenderer() {}

    public void renderPlant(IAgriPlant plant, World world, BlockPos pos, MatrixStack matrix, IRenderTypeBuffer buffer,
                            int light, int overlay, Direction... preferredSides) {
        // TODO
    }
}
