package com.infinityraider.agricraft.plugins.mysticalagriculture;

import com.blakebr0.mysticalagriculture.api.MysticalAgricultureAPI;
import com.blakebr0.mysticalagriculture.api.crop.Crop;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.client.AgriPlantRenderType;
import com.infinityraider.agricraft.api.v1.content.items.IAgriSeedItem;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowable;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.render.plant.AgriPlantQuadGenerator;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.IntFunction;

public class MysticalAgricultureCompatClient {
    public static void init() {
        registerEventHandler();
        createPlantRenderType();
        registerItemColors();
    }

    private static void registerEventHandler() {
        AgriCraft.instance.proxy().registerEventHandler(MysticalAgriculturePlantSubstitutor.getInstance());
    }

    // Creates the render type for Mystical Agriculture crops
    private static void createPlantRenderType() {
        AgriPlantRenderType.create(
                "MYSTICAL_AGRICULTURE",
                AgriPlantRenderType.Identifiers.predicate(ImmutableList.of("mysticalagriculture", "mystical_agriculture")),
                MysticalAgricultureCompatClient::bakeQuadsForMystical
        );
    }

    @Nonnull
    private static List<BakedQuad> bakeQuadsForMystical(IAgriGrowable growable, IAgriGrowthStage stage, @Nullable Direction face, IntFunction<TextureAtlasSprite> spriteFunc) {
        if (face != null) {
            return ImmutableList.of();
        }
        ITessellator tessellator = AgriPlantQuadGenerator.getInstance().getTessellator();
        TextureAtlasSprite sprite = spriteFunc.apply(0);

        tessellator.startDrawingQuads();
        tessellator.setFace((Direction) null);

        tessellator.pushMatrix();

        tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.NORTH, sprite, 4.001F, 0, 0, 16, 16);
        tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.NORTH, sprite, 3.999F, 0, 0, 16, 16);

        tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.EAST, sprite, 4.001F, 0, 0, 16, 16);
        tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.EAST, sprite, 3.999F, 0, 0, 16, 16);

        tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.NORTH, sprite, 12.001F, 0, 0, 16, 16);
        tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.NORTH, sprite, 11.999F, 0, 0, 16, 16);

        tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.EAST, sprite, 12.001F, 0, 0, 16, 16);
        tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.EAST, sprite, 11.999F, 0, 0, 16, 16);

        if (stage.isFinal()) {
            IAgriPlant plant = ((IAgriPlant) growable);
            int flowerColor = MysticalAgricultureCompatClient.colorForFlower(plant.getId());

            TextureAtlasSprite flowerSprite = spriteFunc.apply(1);

            if (flowerColor != -1) {
                tessellator.setColorRGB(((flowerColor >> 16) & 0xFF) / 255.0F, ((flowerColor >> 8) & 0xFF) / 255.0F, ((flowerColor) & 0xFF) / 255.0F);
            }

            tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.NORTH, flowerSprite, 4.001F, 0, 0, 16, 16);
            tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.NORTH, flowerSprite, 3.999F, 0, 0, 16, 16);

            tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.EAST, flowerSprite, 4.001F, 0, 0, 16, 16);
            tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.EAST, flowerSprite, 3.999F, 0, 0, 16, 16);

            tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.NORTH, flowerSprite, 12.001F, 0, 0, 16, 16);
            tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.NORTH, flowerSprite, 11.999F, 0, 0, 16, 16);

            tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.EAST, flowerSprite, 12.001F, 0, 0, 16, 16);
            tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.EAST, flowerSprite, 11.999F, 0, 0, 16, 16);
        }

        tessellator.translate(0, 12.0F/16.0F, 0);

        tessellator.popMatrix();
        List<BakedQuad> quads = tessellator.getQuads();
        tessellator.draw();
        return quads;
    }

    // Registers the item colors for recoloring dynamically colored MA seeds
    private static void registerItemColors() {
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (stack.getItem() instanceof IAgriSeedItem) {
                IAgriSeedItem seedItem = (IAgriSeedItem) stack.getItem();
                String id = seedItem.getPlant(stack).getId();
                int color = colorForFlower(id);
                return color == -1 ? -1 : (0xFF << 24) + color;
            }
            return -1;
        }, AgriApi.getAgriContent().getItems().getSeedItem().toItem());
    }

    /**
     * Search the flower's color of a mystical agriculture plant.
     *
     * @param plantId the plant id.
     * @return the color of the flower, or -1 if the plant is not in the mystical agriculture plant registry or the plant don't have a color.
     */
    @SuppressWarnings("deprecation")
    public static int colorForFlower(String plantId) {
        Crop mysticalCrop = getCropFromPlantId(plantId);
        return mysticalCrop == null ? -1 : mysticalCrop.getFlowerColor();
    }

    /**
     * Search the seed's color of a mystical agriculture plant.
     *
     * @param plantId the plant id.
     * @return the color of the flower, or -1 if the plant is not in the mystical agriculture plant registry or the plant don't have a color.
     */
    @SuppressWarnings("deprecation")
    public static int colorForSeed(String plantId) {
        Crop crop = getCropFromPlantId(plantId);
        return crop != null && crop.isSeedColored() ? crop.getSeedColor() : -1;
    }

    @SuppressWarnings("deprecation")
    public static Crop getCropFromPlantId(String plantId) {
        String[] split = plantId.split(":");
        if(split.length > 1) {
            String path = split[1];
            // We assume the plant id is "<modid>:<resource>_plant"
            ResourceLocation location = new ResourceLocation(split[0], path.substring(0, path.length() - 6)); // remove the "_plant" suffix
            return MysticalAgricultureAPI.getCropRegistry().getCropById(location);
        } else {
            AgriCraft.instance.getLogger().error("Invalid plant id: " + plantId);
            return null;
        }
    }
}
