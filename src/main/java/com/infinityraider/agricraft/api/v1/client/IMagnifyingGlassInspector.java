package com.infinityraider.agricraft.api.v1.client;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Class to handle inspection of blocks with the magnifying glass.
 * Instances are registered with IMagnifyingGlassInspector.registerInspector()
 */
@OnlyIn(Dist.CLIENT)
public interface IMagnifyingGlassInspector {
    /**
     * Checks if this instance is used to handle inspection of the given block pos
     * Called on the client thread.
     *
     * @param world the world
     * @param pos the position
     * @param player the player
     * @return true if this inspector can inspect the block at the given position
     */
    boolean canInspect(Level world, BlockPos pos, Player player);

    /**
     * Checks if this instance is used to handle inspection of the given Entity
     * Called on the client thread.
     *
     * @param world the world
     * @param entity the entity
     * @param player the player
     * @return true if this inspector can inspect the block at the given position
     */
    boolean canInspect(Level world, Entity entity, Player player);

    /**
     * Callback for when the player starts inspecting with this inspector.
     * Use to cache data.
     * Called on the client thread.
     *
     * @param world the world
     * @param pos the position
     * @param player the player
     */
    void onInspectionStart(Level world, BlockPos pos, Player player);

    /**
     * Callback for when the player starts inspecting with this inspector.
     * Use to cache data.
     * Called on the client thread.
     *
     * @param world the world
     * @param entity the entity
     * @param player the player
     */
    void onInspectionStart(Level world, Entity entity, Player player);

    /**
     * Callback for ticks when the player is inspecting with this inspector
     * Called on the client thread
     *
     * @param world the world
     * @param pos the position
     * @param player the player
     * @return true to allow continue inspecting, false to force stop inspecting
     */
    boolean onInspectionTick(Level world, BlockPos pos, Player player);

    /**
     * Callback for ticks when the player is inspecting with this inspector
     * Called on the client thread
     *
     * @param world the world
     * @param entity the entity
     * @param player the player
     * @return true to allow continue inspecting, false to force stop inspecting
     */
    boolean onInspectionTick(Level world, Entity entity, Player player);

    /**
     * Callback for when the player stops inspecting with this inspector.
     * Use to clear cached data.
     * Called on the client thread
     *
     * @param world the world
     * @param pos the position
     * @param player the player
     */
    void onInspectionEnd(Level world, BlockPos pos, Player player);

    /**
     * Callback for when the player stops inspecting with this inspector.
     * Use to clear cached data.
     * Called on the client thread
     *
     * @param world the world
     * @param entity the entity, can be null if the inspection ended due to the entity de-spawning
     * @param player the player
     */
    void onInspectionEnd(Level world, @Nullable Entity entity, Player player);

    /**
     * Callback for when the player scrolls the mouse
     * @param delta the scroll delta
     */
    void onMouseScroll(int delta);

    /**
     * Method to render additional "things" when the player is inspecting with this inspector
     * Called on the render thread
     *
     * @param transforms MatrixStack positioned at the inspected position
     * @param partialTick the partial render tick
     * @param entity the entity being inspected, can be null if the player is inspecting a block
     */
    void doInspectionRender(PoseStack transforms, float partialTick, @Nullable Entity entity);

    /**
     * Registers an inspector instance
     * @param inspector the inspector to register
     */
    static void registerInspector(IMagnifyingGlassInspector inspector) {
        AgriApi.registerMagnifyingGlassInspector(inspector);
    }
}
