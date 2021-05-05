package com.infinityraider.agricraft.plugins.jei;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.handler.JournalViewPointHandler;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SoilPropertyIconRenderer implements IRenderUtilities {
    private static final SoilPropertyIconRenderer INSTANCE = new SoilPropertyIconRenderer();

    public static SoilPropertyIconRenderer getInstance() {
        return INSTANCE;
    }

    private final ResourceLocation texture_humidity = JournalViewPointHandler.JournalData.Textures.HUMIDITY_FILLED;
    private final ResourceLocation texture_acidity = JournalViewPointHandler.JournalData.Textures.ACIDITY_FILLED;
    private final ResourceLocation texture_nutrients = JournalViewPointHandler.JournalData.Textures.NUTRIENTS_FILLED;

    private final int[] dxHumidity = {8, 8, 10, 10, 10, 7};
    private final int[] dxAcidity = {7, 8, 7, 8, 8, 8, 6};
    private final int[] dxNutrients = {6, 8, 9, 9, 11, 10};

    private SoilPropertyIconRenderer() {}

    public void drawHumidityIcon(IAgriSoil.SoilProperty property, MatrixStack transforms, int x, int y, double mX, double mY) {
        this.drawIcon(property, transforms, x, y, mX, mY, this.texture_humidity);
    }

    public void drawAcidityIcon(IAgriSoil.SoilProperty property, MatrixStack transforms, int x, int y, double mX, double mY) {
        this.drawIcon(property, transforms, x, y, mX, mY, this.texture_acidity);
    }


    public void drawNutrientsIcon(IAgriSoil.SoilProperty property, MatrixStack transforms, int x, int y, double mX, double mY) {
        this.drawIcon(property, transforms, x, y, mX, mY, this.texture_nutrients);
    }

    public void drawIcon(IAgriSoil.SoilProperty property, MatrixStack transforms, int x, int y, double mX, double mY, ResourceLocation texture) {
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
            AbstractGui.blit(transforms, x + x1, y, w, 12, x1, 0, w, 12, 53, 12);
            // Draw the tooltip if needed
            if(mX >= x + x1 && mX < x + x1 + w && mY >= y && mY <= y + 12) {
                List<ITextComponent> tooltip = ImmutableList.of(property.getDescription());
                this.drawTooltip(transforms, tooltip, mX, mY + 12);
            }
        }
    }

    protected void drawTooltip(MatrixStack transforms, List<ITextComponent> tooltip, double x, double y) {
        int w = this.getScaledWindowWidth();
        int h = this.getScaledWindowHeight();
        GuiUtils.drawHoveringText(transforms, tooltip, (int) x, (int) y, w, h, -1, this.getFontRenderer());
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
