package com.infinityraider.agricraft.plugins.mysticalagriculture;

import com.blakebr0.mysticalagriculture.api.MysticalAgricultureAPI;
import com.blakebr0.mysticalagriculture.api.crop.ICrop;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.client.AgriPlantRenderType;
import com.infinityraider.agricraft.api.v1.content.items.IAgriSeedItem;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowable;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.agricraft.render.plant.AgriPlantQuadGenerator;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

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
        List<BakedQuad> baseQuads = AgriPlantQuadGenerator.getInstance().bakeQuadsForPlusPattern(growable, stage, face, spriteFunc);
        if (stage.isFinal()) {
            IAgriPlant plant = ((IAgriPlant) growable);
            int flowerColor = MysticalAgricultureCompatClient.colorForFlower(plant.getId());

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

            tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.NORTH, sprite, 4.001F, 0, 0, 16, 16);
            tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.NORTH, sprite, 3.999F, 0, 0, 16, 16);

            tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.EAST, sprite, 4.001F, 0, 0, 16, 16);
            tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.EAST, sprite, 3.999F, 0, 0, 16, 16);

            tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.NORTH, sprite, 12.001F, 0, 0, 16, 16);
            tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.NORTH, sprite, 11.999F, 0, 0, 16, 16);

            tessellator.drawScaledFaceDouble(-2, 0, 10, 12, Direction.EAST, sprite, 12.001F, 0, 0, 16, 16);
            tessellator.drawScaledFaceDouble(6, 0, 18, 12, Direction.EAST, sprite, 11.999F, 0, 0, 16, 16);

            tessellator.popMatrix();

            List<BakedQuad> flowerQuads = tessellator.getQuads();
            tessellator.draw();

            return new ImmutableList.Builder<BakedQuad>().addAll(baseQuads).addAll(flowerQuads).build();
        } else {
            return baseQuads;
        }
    }

    // Registers the item colors for recoloring dynamically colored MA seeds
    private static void registerItemColors() {
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (stack.getItem() instanceof IAgriSeedItem) {
                IAgriSeedItem seedItem = (IAgriSeedItem) stack.getItem();
                String id = seedItem.getPlant(stack).getId();
                System.out.println("searching color for " + id);
                if (!id.startsWith(Names.Mods.MYSTICAL_AGRICULTURE)) {
                    return -1;
                }
                int color = colorForFlower(id);
                return color == -1 ? -1 : (0xFF << 24) + color;
            }
            return -1;
        }, AgriItemRegistry.getInstance().seed);
    }

    /**
     * Search the flower's color of a mystical agriculture plant.
     *
     * @param plantId the plant id.
     * @return the color of the flower, or -1 if the plant is not in the mystical agriculture plant registry or the plant don't have a color.
     */
    @SuppressWarnings("deprecation")
    public static int colorForFlower(String plantId) {
        ICrop mysticalCrop = getCropFromPlantId(plantId);
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
        ICrop crop = getCropFromPlantId(plantId);
        return crop != null && crop.isSeedColored() ? crop.getSeedColor() : -1;
    }

    @SuppressWarnings("deprecation")
    public static ICrop getCropFromPlantId(String plantId) {
        String path = plantId.split(":")[1];
        // We assume the plant id is "<modid>:<resource>_plant"
        ResourceLocation location = new ResourceLocation(Names.Mods.MYSTICAL_AGRICULTURE, path.substring(0, path.length() - "_plant".length()));
        return MysticalAgricultureAPI.getCropRegistry().getCropById(location);
    }
}
