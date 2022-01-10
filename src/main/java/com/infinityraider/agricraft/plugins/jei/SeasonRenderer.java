package com.infinityraider.agricraft.plugins.jei;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.render.items.journal.JournalDataDrawerPlant;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;

public class SeasonRenderer implements IRenderUtilities {
    private static final SeasonRenderer INSTANCE = new SeasonRenderer();

    public static SeasonRenderer getInstance() {
        return INSTANCE;
    }

    private final ResourceLocation texture = JournalDataDrawerPlant.Textures.SEASONS_FILLED;

    private SeasonRenderer() {
    }

    public void renderSeasons(MatrixStack transforms, int x, int y, Predicate<AgriSeason> predicate) {
        if (AgriApi.getSeasonLogic().isActive()) {
            this.bindTexture(this.texture);
            AgriSeason.stream().filter(predicate).forEach(season -> {
                int i = season.ordinal();
                AbstractGui.blit(transforms, x, y + 13 * i, 12, 12, 0, 12 * i, 12, 12, 12, 48);
            });
        }
    }
}
