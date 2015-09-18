package com.InfinityRaider.AgriCraft.gui.journal;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.InfinityRaider.AgriCraft.gui.Component;
import com.InfinityRaider.AgriCraft.reference.Reference;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class JournalPageTitle extends JournalPage {
    @Override
    public ResourceLocation getForeground() {
        return new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/journal/GuiJournalFrontPage.png");
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
