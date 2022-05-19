package com.infinityraider.agricraft.render.plant.gui;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.render.items.journal.JournalDataDrawerPlant;
import com.infinityraider.infinitylib.utility.TooltipRegion;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class SoilPropertyIconRenderer implements IRenderUtilities {
    private static final SoilPropertyIconRenderer INSTANCE = new SoilPropertyIconRenderer();

    public static SoilPropertyIconRenderer getInstance() {
        return INSTANCE;
    }

    private final ResourceLocation texture_humidity = JournalDataDrawerPlant.Textures.HUMIDITY_FILLED;
    private final ResourceLocation texture_acidity = JournalDataDrawerPlant.Textures.ACIDITY_FILLED;
    private final ResourceLocation texture_nutrients = JournalDataDrawerPlant.Textures.NUTRIENTS_FILLED;

    private final int[] dxHumidity = {8, 8, 10, 10, 10, 7};
    private final int[] dxAcidity = {7, 8, 7, 8, 8, 8, 6};
    private final int[] dxNutrients = {6, 8, 9, 9, 11, 10};

    private SoilPropertyIconRenderer() {}

    public void drawHumidityIcon(IAgriSoil.SoilProperty property, PoseStack transforms, int x, int y, double mX, double mY) {
        this.drawIcon(property, transforms, x, y, mX, mY, this.texture_humidity);
    }

    public void drawAcidityIcon(IAgriSoil.SoilProperty property, PoseStack transforms, int x, int y, double mX, double mY) {
        this.drawIcon(property, transforms, x, y, mX, mY, this.texture_acidity);
    }


    public void drawNutrientsIcon(IAgriSoil.SoilProperty property, PoseStack transforms, int x, int y, double mX, double mY) {
        this.drawIcon(property, transforms, x, y, mX, mY, this.texture_nutrients);
    }

    public void drawIcon(IAgriSoil.SoilProperty property, PoseStack transforms, int x, int y, double mX, double mY, ResourceLocation texture) {
        if(property.isValid()) {
            // Determine coordinates
            int index = property.ordinal();
            int[] offsets = this.getOffsets(property);
            int x1 = 0;
            for(int i = 0; i < index; i++) {
                x1 += offsets[i];
            }
            int w = offsets[index];
            // Draw the icon
            this.bindTexture(texture);
            Screen.blit(transforms, x + x1, y, w, 12, x1, 0, w, 12, 53, 12);
        }
    }

    public <T> void defineHumidityTooltips(Consumer<TooltipRegion<T>> consumer, int x, int y) {
        this.defineTooltips(IAgriSoil.Humidity.values(), consumer, x, y);
    }

    public <T> void defineAcidityTooltips(Consumer<TooltipRegion<T>> consumer, int x, int y) {
        this.defineTooltips(IAgriSoil.Acidity.values(), consumer, x, y);
    }

    public <T> void defineNutrientsTooltips(Consumer<TooltipRegion<T>> consumer, int x, int y) {
        this.defineTooltips(IAgriSoil.Nutrients.values(), consumer, x, y);
    }

    private <T> void defineTooltips(IAgriSoil.SoilProperty[] properties, Consumer<TooltipRegion<T>> consumer, int x, int y) {
        Arrays.stream(properties).filter(IAgriSoil.SoilProperty::isValid)
                .forEach(property -> {
                    int index = property.ordinal();
                    int[] offsets = this.getOffsets(property);
                    int x1 = 0;
                    for(int i = 0; i < index; i++) {
                        x1 += offsets[i];
                    }
                    int w = offsets[index];
                    List<Component> tooltip = ImmutableList.of(property.getDescription());
                    consumer.accept(new TooltipRegion<>(tooltip, x + x1, y, x + x1 + w, y + 12));
                });
    }

    protected int[] getOffsets(IAgriSoil.SoilProperty property) {
        if(property instanceof IAgriSoil.Humidity) {
            return dxHumidity;
        }
        if(property instanceof IAgriSoil.Acidity) {
            return dxAcidity;
        } else {
            return dxNutrients;
        }
    }
}
