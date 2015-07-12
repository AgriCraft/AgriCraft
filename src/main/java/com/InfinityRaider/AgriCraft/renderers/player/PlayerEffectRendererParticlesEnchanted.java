package com.InfinityRaider.AgriCraft.renderers.player;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class PlayerEffectRendererParticlesEnchanted extends PlayerEffectRendererParticles {
    @Override
    protected ResourceLocation getParticleTexture() {
        return null;
    }

    ArrayList<String> getDisplayNames() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Gideonseymour");
        return list;
    }
}
