package com.infinityraider.agricraft.handler;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriPlant;
import com.agricraft.agricore.plant.AgriWeed;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.render.blocks.TileEntityIrrigationChannelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
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
        if(event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS)) {
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
        // Jsons
        AgriCore.getPlants().getAllElements()
                .stream()
                .map(AgriPlant::getSeedModel)
                .map(this::toResourceLocation)
                .forEach(ForgeModelBakery::addSpecialModel);
        // no plant instance
        ForgeModelBakery.addSpecialModel(NoPlant.getInstance().getSeedModel());
        // seed bag models
        ForgeModelBakery.addSpecialModel(new ResourceLocation(AgriCraft.instance.getModId(), "item/agri_seed_bag_empty"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(AgriCraft.instance.getModId(), "item/agri_seed_bag_partial"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(AgriCraft.instance.getModId(), "item/agri_seed_bag_full"));
        // channel valve hand Wheel
        ForgeModelBakery.addSpecialModel(TileEntityIrrigationChannelRenderer.MODEL_HANDWHEEL);
    }

    private ResourceLocation toResourceLocation(String string) {
        if(string.contains("#")) {
            return new ModelResourceLocation(string);
        } else {
            return new ResourceLocation(string);
        }
    }
}
