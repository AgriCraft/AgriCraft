package com.InfinityRaider.AgriCraft.renderers.player;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;

@SideOnly(Side.CLIENT)
public abstract class PlayerEffectRenderer {
    private String UUID;

    PlayerEffectRenderer(String UUID) {
        this.UUID = UUID;
    }

    String getUUID() {
        return new String(this.UUID);
    }

    abstract void renderEffects(EntityPlayer player, RenderPlayer renderer, float tick);
}
