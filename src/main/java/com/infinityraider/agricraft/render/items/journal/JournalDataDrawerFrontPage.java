package com.infinityraider.agricraft.render.items.journal;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.impl.v1.journal.FrontPage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JournalDataDrawerFrontPage extends JournalDataDrawerBase<FrontPage> {
    private static final ResourceLocation BACKGROUND_FRONT_RIGHT = new ResourceLocation(
            AgriCraft.instance.getModId().toLowerCase(),
            "textures/journal/front_page.png"
    );

    @Override
    public ResourceLocation getId() {
        return FrontPage.INSTANCE.getDataDrawerId();
    }

    @Override
    public void drawLeftSheet(FrontPage page, IPageRenderContext context, PoseStack transforms, ItemStack stack, IAgriJournalItem journal) {

    }

    @Override
    public void drawRightSheet(FrontPage page, IPageRenderContext context, PoseStack transforms, ItemStack stack, IAgriJournalItem journal) {
        context.drawFullPageTexture(transforms, BACKGROUND_FRONT_RIGHT);
    }
}
