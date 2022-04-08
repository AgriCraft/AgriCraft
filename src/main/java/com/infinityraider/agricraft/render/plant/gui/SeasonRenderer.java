package com.infinityraider.agricraft.render.plant.gui;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.render.items.journal.JournalDataDrawerPlant;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Predicate;

public class SeasonRenderer implements IRenderUtilities {
    private static final SeasonRenderer INSTANCE = new SeasonRenderer();

    public static SeasonRenderer getInstance() {
        return INSTANCE;
    }

    private final ResourceLocation texture = JournalDataDrawerPlant.Textures.SEASONS_FILLED;

    private SeasonRenderer() {
    }

    public void renderSeasons(PoseStack transforms, int x, int y, Predicate<AgriSeason> predicate) {
        if (AgriApi.getSeasonLogic().isActive()) {
            this.bindTexture(this.texture);
            AgriSeason.stream().filter(predicate).forEach(season -> {
                int i = season.ordinal();
                Screen.blit(transforms, x, y + 13 * i, 12, 12, 0, 12 * i, 12, 12, 12, 48);
            });
        }
    }
}
