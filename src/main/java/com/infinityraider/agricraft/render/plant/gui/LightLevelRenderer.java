package com.infinityraider.agricraft.render.plant.gui;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.utility.TooltipRegion;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class LightLevelRenderer implements IRenderUtilities {

    private static final LightLevelRenderer INSTANCE = new LightLevelRenderer();

    public static LightLevelRenderer getInstance() {
        return INSTANCE;
    }

    private final ResourceLocation texture = new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/light_levels.png");

    private LightLevelRenderer() {}

    public void renderLightLevels(PoseStack transforms, int x, int y, double mX, double mY, Predicate<Integer> predicate) {
        this.bindTexture(this.texture);
        for(int i = 15; i >= 0; i--) {
            int y_i = y + 3 * (15 - i);
            if(predicate.test(i)) {
                Screen.blit(transforms, x, y_i, 3, 3, 0, 3 * (15 - i), 3, 3, 3, 48);
            }
        }
    }

    public <T> void defineTooltips(Consumer<TooltipRegion<T>> consumer, int x, int y) {
        for(int i = 15; i >= 0; i--) {
            int y_i = y + 3 * (15 - i);
            List<Component> tooltip = ImmutableList.of(new TextComponent("")
                    .append(AgriToolTips.LIGHT)
                    .append(new TextComponent(": " + i)));
            consumer.accept(new TooltipRegion<>(tooltip, x, y_i, x + 3, y_i + 3));
        }
    }
}
