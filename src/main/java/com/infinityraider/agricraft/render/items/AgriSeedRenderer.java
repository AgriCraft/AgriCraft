package com.infinityraider.agricraft.render.items;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        ResourceLocation rl = plant.getSeedModel();
        if(rl.toString().contains("wheat")) {
            this.debug();
        }
        IBakedModel model = this.getModelManager().getModel(rl);
        IVertexBuilder vertexBuilder = ItemRenderer.getEntityGlintVertexBuilder(buffer, RenderTypeLookup.func_239219_a_(stack, true), true, stack.hasEffect());
        this.getItemRenderer().renderModel(model, stack, light, overlay, transforms, vertexBuilder);
    }

    private Map<ResourceLocation, IBakedModel> modelRegistry;

    @SuppressWarnings("unchecked")
    private void debug() {
        if(this.modelRegistry == null) {
            try {
                Field field = ModelManager.class.getDeclaredField("modelRegistry");
                field.setAccessible(true);
                this.modelRegistry = (Map<ResourceLocation, IBakedModel>) field.get(this.getModelManager());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<Map.Entry<ResourceLocation, IBakedModel>> filtered = this.modelRegistry.entrySet().stream()
                .filter(entry -> entry.getKey().toString().contains("wheat"))
                .collect(Collectors.toList());
        boolean debug = true;
    }
}
