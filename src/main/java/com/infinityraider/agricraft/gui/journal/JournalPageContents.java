package com.infinityraider.agricraft.gui.journal;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.reference.Reference;


@SideOnly(Side.CLIENT)
public class JournalPageContents extends JournalPage {

    @Override
    public ResourceLocation getForeground() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalTableOfContents.png");
    }

}
