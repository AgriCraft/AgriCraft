package com.infinityraider.agricraft.client.gui.journal;

import com.infinityraider.agricraft.client.gui.Component;
import com.infinityraider.agricraft.reference.Reference;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class JournalPageContents extends JournalPage {
    @Override
    public ResourceLocation getForeground() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalTableOfContents.png");
    }

    @Override
    public ArrayList<String> getTooltip(int x, int y) {
        return null;
    }

    @Override
    public ArrayList<Component<String>> getTextComponents() {
        return null;
    }

    @Override
    public ArrayList<Component<ItemStack>> getItemComponents() {
        return null;
    }

    @Override
    public ArrayList<Component<ResourceLocation>> getTextureComponents() {
        return null;
    }

    @Override
    public int getPagesToBrowseOnMouseClick(int x, int y) {
        return 0;
    }
}
