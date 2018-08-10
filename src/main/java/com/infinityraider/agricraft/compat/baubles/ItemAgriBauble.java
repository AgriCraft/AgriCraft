/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.infinityraider.agricraft.compat.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.infinitylib.item.IItemWithModel;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.item.ItemStack;

/**
 *
 * @author Ryan
 */
public final class ItemAgriBauble extends ItemBase implements IBauble, IItemWithModel {
    
    public ItemAgriBauble() {
        super("agri_bauble");
        this.setMaxStackSize(1);
        this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.CHARM;
    }
    
    @Override
    public boolean isEnabled() {
        return BaublesPlugin.ENABLE_AGRI_BAUBLE;
    }
    
}
