package com.infinityraider.agricraft.gui.journal;

import com.infinityraider.agricraft.reference.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class JournalPageContents extends JournalPage {

	@Override
	public ResourceLocation getForeground() {
		return new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalTableOfContents.png");
	}

	@Override
	public int getPagesToBrowseOnMouseClick(int x, int y) {
		return 0;
	}

}
