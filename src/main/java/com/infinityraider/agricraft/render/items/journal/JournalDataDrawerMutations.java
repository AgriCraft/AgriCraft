package com.infinityraider.agricraft.render.items.journal;

import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.impl.v1.journal.MutationsPage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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
    public void drawLeftSheet(MutationsPage page, IPageRenderContext context, PoseStack transforms, ItemStack stack, IAgriJournalItem journal) {
        int posX = 10;
        int posY = 6;
        int dy = 20;
        for (List<IAgriPlant> plants : page.getMutationsLeft()) {
            this.drawMutation(context, transforms, posX, posY, plants);
            posY += dy;
        }
    }

    @Override
    public void drawRightSheet(MutationsPage page, IPageRenderContext context, PoseStack transforms, ItemStack stack, IAgriJournalItem journal) {
        int posX = 10;
        int posY = 6;
        int dy = 20;
        for (List<IAgriPlant> plants : page.getMutationsRight()) {
            this.drawMutation(context, transforms, posX, posY, plants);
            posY += dy;
        }

    }

    @Override
    public void drawTooltipLeft(MutationsPage page, IPageRenderContext context, PoseStack transforms, int x, int y) {
        int posX = 10;
        int posY = 6;
        int dy = 20;
        for (List<IAgriPlant> plants : page.getMutationsLeft()) {
            context.drawTooltip(transforms, this.getTextLines(x, y, posX, posY, plants), x, y);
            posY += dy;
        }
    }

    @Override
    public void drawTooltipRight(MutationsPage page, IPageRenderContext context, PoseStack transforms, int x, int y) {
        int posX = 10;
        int posY = 6;
        int dy = 20;
        for (List<IAgriPlant> plants : page.getMutationsRight()) {
            context.drawTooltip(transforms, this.getTextLines(x, y, posX, posY, plants), x, y);
            posY += dy;
        }
    }

    private List<Component> getTextLines(int x, int y, int posX, int posY, List<IAgriPlant> plants) {
        if (posX + 1 <= x && x <= posX + 17 && posY + 1 <= y && y <= posY + 17) {
            return Collections.singletonList(plants.get(0).getTooltip());
        } else if (posX + 35 <= x && x <= posX + 51 && posY + 1 <= y && y <= posY + 17) {
            return Collections.singletonList(plants.get(1).getTooltip());
        } else if (posX + 69 <= x && x <= posX + 85 && posY + 1 <= y && y <= posY + 17) {
            return Collections.singletonList(plants.get(2).getTooltip());
        }
        return Collections.emptyList();
    }

}
