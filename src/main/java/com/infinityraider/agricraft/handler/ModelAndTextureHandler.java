package com.infinityraider.agricraft.handler;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriPlant;
import com.agricraft.agricore.plant.AgriWeed;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class ModelAndTextureHandler {
    private static final ModelAndTextureHandler INSTANCE = new ModelAndTextureHandler();

    public static ModelAndTextureHandler getInstance() {
        return INSTANCE;
    }

    private ModelAndTextureHandler() {}

    // Used to stitch the textures used in plant rendering
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
        if(event.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE)) {
            Stream.concat(
                    AgriCore.getPlants().getAllElements().stream().map(AgriPlant::getTexture),
                    AgriCore.getWeeds().getAllElements().stream().map(AgriWeed::getTexture)
            ).flatMap(tex -> tex.getAllTextures().stream().distinct())
                    .map(ResourceLocation::new)
                    .forEach(event::addSprite);
        }
    }

    // Used to add the models used in seed rendering
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onModelLoadEvent(ModelRegistryEvent event) {
        ModelLoader loader = ModelLoader.instance();
        AgriCore.getPlants().getAllElements()
                .stream()
                .map(AgriPlant::getSeedModel)
                .map(this::toResourceLocation)
                .forEach(ModelLoader::addSpecialModel);
    }

    private ResourceLocation toResourceLocation(String string) {
        if(string.contains("#")) {
            return new ModelResourceLocation(string);
        } else {
            return new ResourceLocation(string);
        }
    }
}
