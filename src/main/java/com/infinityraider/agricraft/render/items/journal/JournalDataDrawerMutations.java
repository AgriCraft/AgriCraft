package com.infinityraider.agricraft.render.items.journal;

import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.impl.v1.journal.MutationsPage;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
}
