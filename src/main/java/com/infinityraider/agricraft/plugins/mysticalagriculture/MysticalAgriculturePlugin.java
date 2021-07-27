package com.infinityraider.agricraft.plugins.mysticalagriculture;

import com.blakebr0.mysticalagriculture.api.MysticalAgricultureAPI;
import com.blakebr0.mysticalagriculture.api.crop.Crop;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.client.AgriPlantRenderType;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowable;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.agricraft.render.plant.AgriPlantQuadGenerator;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.IntFunction;

@AgriPlugin(modId = Names.Mods.MYSTICAL_AGRICULTURE)
public class MysticalAgriculturePlugin implements IAgriPlugin {

    @Override
    public boolean isEnabled() {
        return true;
//        return AgriCraft.instance.getConfig().enableMysticalAgricultureCompat();
    }

    @Override
    public String getId() {
        return Names.Mods.MYSTICAL_AGRICULTURE;
    }

    @Override
    public String getDescription() {
        return "Mystical Agriculture compatibility";
    }

    @Override
    public void onClientSetupEvent(FMLClientSetupEvent event) {
        System.out.println("from client setup");
        AgriPlantRenderType.create("MYSTICAL_AGRICULTURE", AgriPlantRenderType.Identifiers.predicate(ImmutableList.of("mysticalagriculture", "mystical_agriculture")), this::bakeQuadsForMystical);
    }

    @Nonnull
    public List<BakedQuad> bakeQuadsForMystical(IAgriGrowable growable, IAgriGrowthStage stage, @Nullable Direction face, IntFunction<TextureAtlasSprite> spriteFunc) {
        if (face != null) {
            return ImmutableList.of();
        }
        List<BakedQuad> baseQuads = AgriPlantQuadGenerator.getInstance().bakeQuadsForCrossPattern(growable, stage, face, spriteFunc);
        if (stage.isFinal()) {
            IAgriPlant plant = ((IAgriPlant) growable);
            String path = plant.getId().split(":")[1];
            //We assume the plant id is "<modid>:<resource>_plant"
            ResourceLocation location = new ResourceLocation(Names.Mods.MYSTICAL_AGRICULTURE, path.substring(0, path.length() - "_plant".length()));
            Crop mysticalCrop = (Crop) MysticalAgricultureAPI.getCropRegistry().getCropById(location);
            int flowerColor = -1;
            if (mysticalCrop != null) {
                flowerColor = mysticalCrop.getFlowerColor();
            }

            TextureAtlasSprite sprite = spriteFunc.apply(1);
            ITessellator tessellator = AgriPlantQuadGenerator.getInstance().getTessellator();

            tessellator.startDrawingQuads();
            tessellator.setFace((Direction) null);

            tessellator.pushMatrix();

            tessellator.translate(0.5f, 0, 0.5f);
            tessellator.rotate(45, 0, 1, 0);
            tessellator.translate(-0.5f, 0, -0.5f);
            if (flowerColor != -1) {
                tessellator.setColorRGB(((flowerColor >> 16) & 0xFF) / 255.0F, ((flowerColor >> 8) & 0xFF) / 255.0F, ((flowerColor) & 0xFF) / 255.0F);
            }

            tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.NORTH, sprite, 8);
            tessellator.drawScaledFaceDouble(0, 0, 16, 16, Direction.EAST, sprite, 8);

            tessellator.popMatrix();

            List<BakedQuad> flowerQuads = tessellator.getQuads();
            tessellator.draw();

            return new ImmutableList.Builder<BakedQuad>().addAll(baseQuads).addAll(flowerQuads).build();
        } else {
            return baseQuads;
        }
    }
}
