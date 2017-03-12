/*
 */
package com.infinityraider.agricraft.api.misc;

import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Interface for harvestable objects.
 *
 *
 */
public interface IAgriHarvestable {

    /**
     * Determines if the object can currently be harvested or not.
     *
     * @return if the object may be harvested.
     */
    boolean canHarvest();

    /**
     * Harvests the object.
     *
     * @param player the player which harvests the crop, may be null if it is
     * harvested by automation.
     * @return if the harvest was successful.
     */
    boolean harvest(@Nullable EntityPlayer player);

    /**
     * Calls the given consumer with the assortment of random fruits gained from
     * harvesting this object. For example, to get a list of the random fruits,
     * pass the consumer list::add.
     *
     * @param fruits the list to add the random fruits to.
     */
    void getFruits(@Nonnull Consumer<ItemStack> fruits);

}
