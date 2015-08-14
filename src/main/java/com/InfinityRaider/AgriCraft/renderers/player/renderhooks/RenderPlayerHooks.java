package com.InfinityRaider.AgriCraft.renderers.player.renderhooks;

import com.InfinityRaider.AgriCraft.reference.Reference;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public final class RenderPlayerHooks {
    private static final String[] modIds = {
            "3DManeuverGear",
            "AgriCraft",
            "Elemancy",
            "ModularArmour"
    };

    private HashMap<String, PlayerEffectRenderer> activeEffectRenderers;
    private static boolean hasInit = false;

    public RenderPlayerHooks() {
        if(!hasInit) {
            hasInit = true;
            this.init();
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("hasInit", true);
            for(String modid:modIds) {
                if(!modid.equals(Reference.MOD_ID)) {
                    FMLInterModComms.sendMessage(modid, "renderHooks", tag);
                }
            }
        }
    }

    public static void onIMCMessage(FMLInterModComms.IMCMessage message) {
        if(hasInit) {
            return;
        }
        if(!message.isNBTMessage()) {
            return;
        }
        if(message.key.equals("renderHooks")) {
            for(String id:modIds) {
                if(id.equals(message.getSender())) {
                    hasInit = true;
                    return;
                }
            }
        }
    }

    private void init() {
        this.registerPlayerEffectRenderer(new PlayerEffectRendererOrbs());
        this.registerPlayerEffectRenderer(new PlayerEffectRendererNavi());
        this.registerPlayerEffectRenderer(new PlayerEffectRendererParticlesEnchanted());
        this.registerPlayerEffectRenderer(new PlayerEffectRendererEntityDragon());
        this.registerPlayerEffectRenderer(new PlayerEffectRendererEntityBat());
        this.registerPlayerEffectRenderer(new PlayerEffectRendererButterfly());
    }

    private void registerPlayerEffectRenderer(PlayerEffectRenderer renderer) {
        if(activeEffectRenderers == null) {
            activeEffectRenderers = new HashMap<String, PlayerEffectRenderer>();
        }
        for(String name:renderer.getDisplayNames()) {
            this.activeEffectRenderers.put(name, renderer);
        }
    }

    @SubscribeEvent
    public void RenderPlayerEffects(RenderPlayerEvent.Specials.Post event) {
        if(activeEffectRenderers.containsKey(event.entityPlayer.getDisplayName())) {
            if(!event.entityPlayer.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer)) {
                GL11.glPushMatrix();
                activeEffectRenderers.get(event.entityPlayer.getDisplayName()).renderEffects(event.entityPlayer, event.renderer, event.partialRenderTick);
                GL11.glPopMatrix();
            }
        }
    }
}
