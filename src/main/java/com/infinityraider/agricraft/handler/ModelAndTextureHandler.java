package com.infinityraider.agricraft.handler;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriPlant;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
            AgriCore.getPlants().getAllElements().stream()
                    .map(AgriPlant::getTexture)
                    .flatMap(tex -> tex.getAllTextures().stream().distinct())
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
                .map(plant -> plant.getTexture().getSeedModel())
                .map(ResourceLocation::new)
                .forEach(ModelLoader::addSpecialModel);
    }
}
