package com.InfinityRaider.AgriCraft.gui.journal;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.InfinityRaider.AgriCraft.gui.Component;
import com.InfinityRaider.AgriCraft.reference.Reference;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class JournalPage {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/journal/GuiJournalBackground.png");

    /** Gets the background texture */
    public static ResourceLocation getBackground() {
        return BACKGROUND;
    }

    /** Gets the foreground texture */
    public abstract ResourceLocation getForeground();

    /** Get the tooltip to render at this location, return null to render nothing */
    public abstract ArrayList<String> getTooltip(int x, int y);

    /** Gets a list of text components to render on this page */
    public abstract ArrayList<Component<String>> getTextComponents();

    /** Gets a list of item components to render on this page */
    public abstract ArrayList<Component<ItemStack>> getItemComponents();

    /** Gets a list of texture components to render on this page */
    public abstract ArrayList<Component<ResourceLocation>> getTextureComponents();

    /** Gets the increment to the page number on a mouseclick, >0 means browse forwards, <0 means browse backwards and 0 means stay on this page */
    public abstract int getPagesToBrowseOnMouseClick(int x, int y);
}
