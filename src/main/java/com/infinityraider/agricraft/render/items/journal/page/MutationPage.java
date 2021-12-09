package com.infinityraider.agricraft.render.items.journal.page;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.render.items.journal.PageRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;

import java.util.List;

public final class MutationPage extends BasePage {
    public static final int LIMIT = 18;

    private final List<List<IAgriPlant>> mutationsLeft;
    private final List<List<IAgriPlant>> mutationsRight;

    public MutationPage(List<List<IAgriPlant>> mutations) {
        int count = mutations.size();
        if(count <= LIMIT/2) {
            this.mutationsLeft = mutations;
            this.mutationsRight = ImmutableList.of();
        } else {
            this.mutationsLeft = mutations.subList(0, LIMIT/2 - 1);
            this.mutationsRight = mutations.subList(LIMIT/2, count - 1);
        }
    }

    @Override
    public void drawLeftSheet(PageRenderer renderer, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        int posX = 10;
        int posY = 6;
        int dy = 20;
        for (List<IAgriPlant> plants : this.mutationsLeft) {
            this.drawMutation(renderer, transforms, posX, posY, plants);
            posY += dy;
        }
    }

    @Override
    public void drawRightSheet(PageRenderer renderer, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        int posX = 10;
        int posY = 6;
        int dy = 20;
        for (List<IAgriPlant> plants : this.mutationsRight) {
            this.drawMutation(renderer, transforms, posX, posY, plants);
            posY += dy;
        }
    }
}
