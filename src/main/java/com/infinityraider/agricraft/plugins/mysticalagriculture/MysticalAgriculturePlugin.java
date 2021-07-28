package com.infinityraider.agricraft.plugins.mysticalagriculture;

import com.blakebr0.mysticalagriculture.api.MysticalAgricultureAPI;
import com.blakebr0.mysticalagriculture.api.crop.Crop;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.client.AgriPlantRenderType;
import com.infinityraider.agricraft.api.v1.content.items.IAgriSeedItem;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.event.JournalRenderEvent;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowable;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.infinityraider.agricraft.plugins.jei.PostJeiRenderStageEvent;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.agricraft.render.plant.AgriPlantQuadGenerator;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.IntFunction;

@AgriPlugin(modId = Names.Mods.MYSTICAL_AGRICULTURE)
public class MysticalAgriculturePlugin implements IAgriPlugin {

    /**
     * Search the flower's color of a mystical agriculture plant.
     *
     * @param plantId the plant id.
     * @return the color of the flower, or -1 if the plant is not in the mystical agriculture plant registry or the plant don't have a color.
     */
    private static int colorFromPlant(String plantId) {
        String path = plantId.split(":")[1];
        //We assume the plant id is "<modid>:<resource>_plant"
        ResourceLocation location = new ResourceLocation(Names.Mods.MYSTICAL_AGRICULTURE, path.substring(0, path.length() - "_plant".length()));
        Crop mysticalCrop = (Crop) MysticalAgricultureAPI.getCropRegistry().getCropById(location);
        int flowerColor = -1;
        if (mysticalCrop != null) {
            flowerColor = mysticalCrop.getFlowerColor();
        }
        return flowerColor;
    }

    @Override
    public boolean isEnabled() {
        return true;
//        return AgriCraft.instance.getConfig().enableMysticalAgricultureCompat();
    }

    @Override
    public String getId() {
        return Names.Mods.MYSTICAL_AGRICULTURE;
    }

//    @Override
//    public void onCommonSetupEvent(FMLCommonSetupEvent event) {
//    }

    @Override
    public String getDescription() {
        return "Mystical Agriculture compatibility";
    }

    @Override
    public void onClientSetupEvent(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(this::onPostJeiRenderStage);
        MinecraftForge.EVENT_BUS.addListener(this::onPostDrawGrowthStage);
        MinecraftForge.EVENT_BUS.addListener(this::registerItemColors);
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
            int flowerColor = colorFromPlant(plant.getId());

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

    public void onPostJeiRenderStage(PostJeiRenderStageEvent event) {
        if (event.getStage().isFinal() && event.getPlant().getId().startsWith(Names.Mods.MYSTICAL_AGRICULTURE)) {
            TextureAtlasSprite sprite = event.getRenderer().getSprite(event.getPlant().getTexturesFor(event.getStage()).get(1));
            int flowerColor = colorFromPlant(event.getPlant().getId());
            if (flowerColor != -1) {
                GL11.glColor3ub((byte) ((flowerColor >> 16) & 0xFF), (byte) ((flowerColor >> 8) & 0xFF), (byte) ((flowerColor) & 0xFF));
            }
            Screen.blit(event.getMatrixStack(), event.getX(), event.getY(), 0, 16, 16, sprite);
//            GL11.glColor3ub((byte) 255, (byte) 255, (byte) 255);
        }
    }

    public void onPostDrawGrowthStage(JournalRenderEvent.PostDrawGrothStages event) {
        if (!event.getPlant().getId().startsWith(Names.Mods.MYSTICAL_AGRICULTURE)) {
            return;
        }

        List<TextureAtlasSprite> stageSprites = event.getStages();
        MatrixStack transforms = event.getMatrixStack();
        List<ResourceLocation> finalLocations = event.getPlant().getTexturesFor(event.getPlant().getFinalStage());
        TextureAtlasSprite flowerSprite = Minecraft.getInstance().getModelManager().getAtlasTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE).getSprite(finalLocations.get(1));

        final float SHADE_LEFT = 0.7F;
        // Position data
        int y0 = 170;
        int delta = 20;
        int rows = stageSprites.size() / 6 + (stageSprites.size() % 6 > 0 ? 1 : 0);
        int columns = stageSprites.size() / rows + (stageSprites.size() % rows > 0 ? 1 : 0);
        // draw stages
        int row = 0;
        int dx = (event.getPageRenderer().getPageWidth() - (16 * (columns))) / (columns + 1);
        for (int i = 0; i < stageSprites.size() - 1; i++) {
            int column = i % columns;
            if (i > 0 && column == 0) {
                row += 1;
            }
            transforms.translate(0, 0, -0.001F);
        }
        int column = (stageSprites.size() - 1) % columns;
        transforms.push();
        transforms.translate(0, 0, -0.001F);
        int flowerColor = colorFromPlant(event.getPlant().getId());
        if (flowerColor != -1) {
            // TODO: 28/07/2021 find a way to color the flower sprite
        }
        event.getPageRenderer().drawTexture(transforms, flowerSprite, dx * (column + 1) + 16 * column, y0 - delta * (rows - row - 1), 16, 16, SHADE_LEFT);
        transforms.pop();
    }

    public void registerItemColors(ColorHandlerEvent.Item event) {
        //this method is not fired for the seed because the baked quads of the model doesn't have a tint index
        event.getItemColors().register((stack, tintIndex) -> {
            if (stack.getItem() instanceof IAgriSeedItem) {
                IAgriSeedItem seedItem = (IAgriSeedItem) stack.getItem();
                String id = seedItem.getPlant(stack).getId();
                System.out.println("searching color for " + id);
                if (!id.startsWith(Names.Mods.MYSTICAL_AGRICULTURE)) {
                    return -1;
                }
                int color = colorFromPlant(id);
                return color == -1 ? -1 : (0xFF << 24) + color;
            }
            return -1;
        }, AgriItemRegistry.getInstance().seed);
    }
}
