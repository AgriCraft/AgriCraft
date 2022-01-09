package com.infinityraider.agricraft.render.items.journal;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.impl.v1.journal.GeneticsPage;
import com.infinityraider.agricraft.impl.v1.stats.AgriStatRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JournalDataDrawerGenetics extends JournalDataDrawerBase<GeneticsPage> {
    private final ITextComponent CROP_BREEDING = new TranslationTextComponent("agricraft.journal.crop_breeding");
    private final ITextComponent PARAGRAPH_L_1 = new TranslationTextComponent("agricraft.journal.crop_breeding.paragraph_1");
    private final ITextComponent PARAGRAPH_L_2 = new TranslationTextComponent("agricraft.journal.crop_breeding.paragraph_2");
    private final ITextComponent PARAGRAPH_L_3 = new TranslationTextComponent("agricraft.journal.crop_breeding.paragraph_3");

    private final ResourceLocation DNA_SCHEMATIC = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(),
            "textures/journal/dna_schematic.png");

    private final ITextComponent STATS = new TranslationTextComponent("agricraft.journal.stats");
    private final ITextComponent PARAGRAPH_R_1 = new TranslationTextComponent("agricraft.journal.stats.paragraph_1");
    private final ITextComponent PARAGRAPH_GROWTH = new TranslationTextComponent("agricraft.journal.stats.growth");
    private final ITextComponent PARAGRAPH_GAIN = new TranslationTextComponent("agricraft.journal.stats.gain");
    private final ITextComponent PARAGRAPH_STRENGTH = new TranslationTextComponent("agricraft.journal.stats.strength");
    private final ITextComponent PARAGRAPH_RESISTANCE = new TranslationTextComponent("agricraft.journal.stats.resistance");
    private final ITextComponent PARAGRAPH_FERTILITY = new TranslationTextComponent("agricraft.journal.stats.fertility");
    private final ITextComponent PARAGRAPH_MUTATIVITY = new TranslationTextComponent("agricraft.journal.stats.mutativity");

    @Override
    public ResourceLocation getId() {
        return GeneticsPage.INSTANCE.getDataDrawerId();
    }

    @Override
    public void drawLeftSheet(GeneticsPage page, IPageRenderContext context, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        float dy = 10;
        float dx = 6;
        float spacing = 4;
        // Title
        dy += context.drawText(transforms, CROP_BREEDING, dx, dy);
        dy += spacing;
        // First paragraph
        dy += context.drawText(transforms, PARAGRAPH_L_1, dx, dy, 0.65F);
        dy += spacing;
        // Second paragraph
        dy += context.drawText(transforms, PARAGRAPH_L_2, dx, dy, 0.65F);
        dy += spacing;
        // Illustration
        context.draw(transforms, DNA_SCHEMATIC, dx, dy, 96, 32);
        dy += (spacing + 32);
        // Third paragraph
        context.drawText(transforms, PARAGRAPH_L_3, dx, dy, 0.65F);
    }

    @Override
    public void drawRightSheet(GeneticsPage page, IPageRenderContext context, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        float dy = 10;
        float dx = 6;
        float spacing = 4;
        // Title
        dy += context.drawText(transforms, STATS, dx, dy);
        dy += spacing;
        // First paragraph
        dy += context.drawText(transforms, PARAGRAPH_R_1, dx, dy, 0.65F);
        dy += spacing;
        // Growth
        if (!AgriCraft.instance.getConfig().isGrowthStatHidden()) {
            dy += context.drawText(transforms, AgriStatRegistry.getInstance().growthStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
                    dx, dy, 0.65F);
            dy += context.drawText(transforms, PARAGRAPH_GROWTH, dx, dy, 0.50F);
            dy += spacing;
        }
        // Gain
        if (!AgriCraft.instance.getConfig().isGainStatHidden()) {
            dy += context.drawText(transforms, AgriStatRegistry.getInstance().gainStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
                    dx, dy, 0.65F);
            dy += context.drawText(transforms, PARAGRAPH_GAIN, dx, dy, 0.50F);
            dy += spacing;
        }
        // Strength
        if (!AgriCraft.instance.getConfig().isStrengthStatHidden()) {
            dy += context.drawText(transforms, AgriStatRegistry.getInstance().strengthStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
                    dx, dy, 0.65F);
            dy += context.drawText(transforms, PARAGRAPH_STRENGTH, dx, dy, 0.50F);
            dy += spacing;
        }
        // Resistance
        if (!AgriCraft.instance.getConfig().isResistanceStatHidden()) {
            dy += context.drawText(transforms, AgriStatRegistry.getInstance().resistanceStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
                    dx, dy, 0.65F);
            dy += context.drawText(transforms, PARAGRAPH_RESISTANCE, dx, dy, 0.50F);
            dy += spacing;
        }
        // Fertility
        if (!AgriCraft.instance.getConfig().isFertilityStatHidden()) {
            dy += context.drawText(transforms, AgriStatRegistry.getInstance().fertilityStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
                    dx, dy, 0.65F);
            dy += context.drawText(transforms, PARAGRAPH_FERTILITY, dx, dy, 0.50F);
            dy += spacing;
        }
        // Mutativity
        if (!AgriCraft.instance.getConfig().isMutativityStatHidden()) {
            dy += context.drawText(transforms, AgriStatRegistry.getInstance().mutativityStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
                    dx, dy, 0.65F);
            context.drawText(transforms, PARAGRAPH_MUTATIVITY, dx, dy, 0.50F);
        }
    }
}
