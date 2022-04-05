package com.infinityraider.agricraft.render.items.journal;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JournalDataDrawerMissing extends JournalDataDrawerBase<IAgriJournalItem.IPage> {
    public static final JournalDataDrawerMissing INSTANCE = new JournalDataDrawerMissing();

    private static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "missing");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void drawLeftSheet(IAgriJournalItem.IPage page, IPageRenderContext context, PoseStack transforms, ItemStack stack, IAgriJournalItem journal) {
        TextComponent text = new TextComponent("Missing Journal Data Drawer: " + page.getDataDrawerId().toString());
        context.drawText(transforms, text, 0, 0);
    }

    @Override
    public void drawRightSheet(IAgriJournalItem.IPage page, IPageRenderContext context, PoseStack transforms, ItemStack stack, IAgriJournalItem journal) {
        TextComponent text = new TextComponent("Missing Journal Data Drawer: " + page.getDataDrawerId().toString());
        context.drawText(transforms, text, 0, 0);
    }
}
