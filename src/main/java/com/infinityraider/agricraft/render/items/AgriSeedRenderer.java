package com.infinityraider.agricraft.render.items;

import com.infinityraider.agricraft.content.core.ItemDynamicAgriSeed;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.item.InfItemRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.*;
import net.minecraft.item.ItemStack;
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
        IBakedModel model = this.getModelManager().getModel(((ItemDynamicAgriSeed) stack.getItem()).getPlant(stack).getSeedModel());
        IVertexBuilder vertexBuilder = ItemRenderer.getEntityGlintVertexBuilder(buffer, RenderTypeLookup.func_239219_a_(stack, true), true, stack.hasEffect());
        this.getItemRenderer().renderModel(model, stack, light, overlay, transforms, vertexBuilder);
    }
}
