package com.infinityraider.agricraft.render.items.journal;

import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.impl.v1.journal.MutationsPage;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class JournalDataDrawerMutations extends JournalDataDrawerBase<MutationsPage> {

    @Override
    public ResourceLocation getId() {
        return MutationsPage.ID;
    }

    @Override
    public void drawLeftSheet(MutationsPage page, IPageRenderContext context, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        int posX = 10;
        int posY = 6;
        int dy = 20;
        for (List<IAgriPlant> plants : page.getMutationsLeft()) {
            this.drawMutation(context, transforms, posX, posY, plants);
            posY += dy;
        }
    }

    @Override
    public void drawRightSheet(MutationsPage page, IPageRenderContext context, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        int posX = 10;
        int posY = 6;
        int dy = 20;
        for (List<IAgriPlant> plants : page.getMutationsRight()) {
            this.drawMutation(context, transforms, posX, posY, plants);
            posY += dy;
        }

    }

    @Override
    public void drawTooltipLeft(MutationsPage page, IPageRenderContext context, MatrixStack transforms, int x, int y) {
        int posX = 10;
        int posY = 6;
        int dy = 20;
        for (List<IAgriPlant> plants : page.getMutationsLeft()) {
            context.drawTooltip(transforms, this.getTextLines(x, y, posX, posY, plants), x, y);
            posY += dy;
        }
    }

    @Override
    public void drawTooltipRight(MutationsPage page, IPageRenderContext context, MatrixStack transforms, int x, int y) {
        int posX = 10;
        int posY = 6;
        int dy = 20;
        for (List<IAgriPlant> plants : page.getMutationsRight()) {
            context.drawTooltip(transforms, this.getTextLines(x, y, posX, posY, plants), x, y);
            posY += dy;
        }
    }

    private List<ITextComponent> getTextLines(int x, int y, int posX, int posY, List<IAgriPlant> plants) {
        if (posX + 1 <= x && x <= posX + 17 && posY + 1 <= y && y <= posY + 17) {
            return Collections.singletonList(plants.get(0).getTooltip());
        } else if (posX + 35 <= x && x <= posX + 51 && posY + 35 <= y && y <= posY + 51) {
            return Collections.singletonList(plants.get(1).getTooltip());
        } else if (posX + 69 <= x && x <= posX + 85 && posY + 69 <= y && y <= posY + 85) {
            return Collections.singletonList(plants.get(2).getTooltip());
        }
        return Collections.emptyList();
    }

}
