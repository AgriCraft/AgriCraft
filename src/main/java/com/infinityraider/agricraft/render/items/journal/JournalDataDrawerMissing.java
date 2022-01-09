package com.infinityraider.agricraft.render.items.journal;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
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
    public void drawLeftSheet(IAgriJournalItem.IPage page, IPageRenderContext context, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        StringTextComponent text = new StringTextComponent("Missing Journal Data Drawer: " + page.getDataDrawerId().toString());
        context.drawText(transforms, text, 0, 0);
    }

    @Override
    public void drawRightSheet(IAgriJournalItem.IPage page, IPageRenderContext context, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        StringTextComponent text = new StringTextComponent("Missing Journal Data Drawer: " + page.getDataDrawerId().toString());
        context.drawText(transforms, text, 0, 0);
    }
}
