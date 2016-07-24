package com.infinityraider.agricraft.gui.journal;

import com.infinityraider.agricraft.gui.component.ComponentRenderer;
import com.infinityraider.agricraft.reference.Reference;
import net.minecraft.util.ResourceLocation;
import com.infinityraider.agricraft.gui.component.GuiComponent;
import com.infinityraider.agricraft.gui.component.GuiComponentBuilder;
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
    public void addComponents(List<GuiComponent> components) {
		components.add(new GuiComponentBuilder<>("agricraft_journal.introduction", 24, 28, 0, 0)
				.setRenderAction(ComponentRenderer::renderComponentText)
				.setScale(0.5)
				.build()
		);
    }

}
