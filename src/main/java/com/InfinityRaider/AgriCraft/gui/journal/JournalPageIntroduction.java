package com.InfinityRaider.AgriCraft.gui.journal;

import com.InfinityRaider.AgriCraft.gui.Component;
import com.InfinityRaider.AgriCraft.reference.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class JournalPageIntroduction extends JournalPage {
    @Override
    public ResourceLocation getForeground() {
        return new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/journal/GuiJournalIntroduction.png");
    }

    @Override
    public ArrayList<String> getTooltip(int x, int y) {
        return null;
    }

    @Override
    public ArrayList<Component<String>> getTextComponents() {
        ArrayList<Component<String>> textComponents = new ArrayList<Component<String>>();
        textComponents.add(new Component<String>(StatCollector.translateToLocal("agricraft_journal.introduction"), 24, 28, 0.5F));
        return textComponents;
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
    public ArrayList<ResourceLocation> getTextureMaps() {
        return new ArrayList<ResourceLocation>();
    }

    @Override
    public ArrayList<Component<IIcon>> getIconComponents(ResourceLocation textureMap) {
        return null;
    }

    @Override
    public int getPagesToBrowseOnMouseClick(int x, int y) {
        return 0;
    }
}
