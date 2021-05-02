package com.infinityraider.agricraft.api.v1.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Enum representing the different types AgriCraft plants / weeds are rendered
 */
@OnlyIn(Dist.CLIENT)
public enum AgriPlantRenderType {
    /** Renders in a hashtag pattern (#); 4 faces parallel with the block faces, similar to Vanilla wheat */
    HASH,

    /** Renders in a cross pattern (x); 2 faces along the diagonals, similar to Vanilla flowers */
    CROSS,

    /** Renders in a plus pattern (+); similar to cross, but instead 4 crosses at each crop stick */
    PLUS,

    /** Renders in a rhombus pattern (◇); 4 faces spanning between the centers of the block faces, only used for weeds */
    RHOMBUS
}
