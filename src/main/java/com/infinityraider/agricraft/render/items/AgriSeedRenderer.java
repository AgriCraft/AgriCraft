package com.infinityraider.agricraft.render.items;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.core.ItemDynamicAgriSeed;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.item.InfItemRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AgriSeedRenderer implements InfItemRenderer, IRenderUtilities {
    private static final AgriSeedRenderer INSTANCE = new AgriSeedRenderer();

    public static AgriSeedRenderer getInstance() {
        return INSTANCE;
    }

    private AgriSeedRenderer() {}

    @Override
    public void render(ItemStack stack, ItemCameraTransforms.TransformType perspective, MatrixStack transforms,
                       IRenderTypeBuffer buffer, int light, int overlay) {
        if(!(stack.getItem() instanceof ItemDynamicAgriSeed)) {
            return;
        }
        ItemDynamicAgriSeed seed = (ItemDynamicAgriSeed) stack.getItem();
        IAgriPlant plant = seed.getPlant(stack);
        ResourceLocation texture = plant.getSeedTexture();
        // TODO
    }
}
