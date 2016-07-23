package com.infinityraider.agricraft.gui.journal;

import com.infinityraider.agricraft.reference.Reference;
import net.minecraft.util.ResourceLocation;
import com.infinityraider.agricraft.gui.component.ComponentText;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class JournalPageIntroduction extends JournalPage {
    @Override
    public ResourceLocation getForeground() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalIntroduction.png");
    }

    @Override
    public void addTextComponents(List<ComponentText> components) {
        components.add(new ComponentText("agricraft_journal.introduction", 24, 28, 0.5F, false));
    }

    @Override
    public int getPagesToBrowseOnMouseClick(int x, int y) {
        return 0;
    }
}
