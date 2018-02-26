package com.infinityraider.agricraft.api.v1.render;

/**
 * This enum is used to decide how AgriCraft handles the rendering of plants on crop sticks
 */
public enum RenderMethod {

    /**
     * This renders the plant like a vanilla flower: in an X-shape. It will render the plant four
     * times, once on each corner. - Primary texture for this is just the texture to use. -
     * Secondary texture is not needed and can be null.
     */
    CROSSED,
    /**
     * This renders the plant like vanilla wheat: in a #-shape. - Primary texture for this is just
     * the texture to use. - Secondary texture is not needed and can be null.
     */
    HASHTAG,
    /**
     * This renders the plant like a stem plant, it will draw a vine texture, emerging from the
     * ground, and a cube for the fruit when mature. - Primary texture for this is the vine, and is
     * the same for all growth stages, except when mature. - Secondary texture is the fruit texture,
     * and is only used when the plant is mature.
     */
    STEM,
    /**
     * This renders the plant similar to CROSSED, but it will grow two blocks tall.
     */
    TALL_CROSSED,
    /**
     * This renders the plant similar to HASHTAG, but it will grow two blocks tall. - Primary
     * texture is used for the lower part. - Secondary texture is used for the upper part, can be
     * null if there is no upper part yet.
     */
    TALL_HASHTAG,
    /**
     * AgriCraft will not render anything, but will forward the call to the ICropPlant object, use
     * this if you want to render the plant yourself. - Primary texture is used for the lower part.
     * - Secondary texture is used for the upper part, can be null if there is no upper part yet.
     */
    CUSTOM;

}
