package com.infinityraider.agricraft.items.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.utility.BaseIcons;
import com.infinityraider.agricraft.utility.CustomWoodType;

/**
 * The root item for all CustomWood blocks.
 */
public class ItemBlockCustomWood extends ItemBlockAgricraft {

    /**
     * The default constructor. A super call to this is generally all that is
     * needed in subclasses.
     *
     * @param block the block associated with this item.
     */
    public ItemBlockCustomWood(Block block) {
        super(block);
        this.hasSubtypes = true;
    }

    @SideOnly(Side.CLIENT)
    public static TextureAtlasSprite getTextureFromStack(ItemStack stack) {
        // TODO: Do something with this...
        return BaseIcons.OAK_PLANKS.getIcon();
    }

    /*
     * Populates the sub-item list.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        this.getSubItems(list);
    }

    /**
     * Populates the sub-item list. This method allows getting sub blocks server
     * side as well (no @side, like
     * {@link #getSubItems(Item, CreativeTabs, List)}). This method is marked
     * for cleaning.
     *
     * @param list the list to populate.
     */
    public void getSubItems(List<ItemStack> list) {
        for (CustomWoodType type : CustomWoodType.getAllTypes()) {
            ItemStack variant = new ItemStack(this.block, 1, 0);
            variant.setTagCompound(type.writeToNBT(new NBTTagCompound()));
            list.add(variant);
        }
    }

    /**
     * Retrieves the block's displayable information. This method does not need
     * to be overridden by most CustomWood blocks.
     * <p>
     * If the block name is not displaying correctly, check the language files
     * and Names.Objects.[blockname]. If that does not correct the issue, ensure
     * that the block overrides both getInternalName() and getTileEntityName()
     * and returns Names.Objects.[blockname].
     * </p>
     * <p>
     * All custom WOOD blocks have a MATERIAL that we want shown, so we make
     * this method final. Some however, has more information they want to add,
     * so we add a addMore() method to OVERRIDE in that event.
     * </p>
     * @param stack
     * @param player
     * @param list
     * @param flag
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        ItemStack material;
        if (stack.getItemDamage() == 0 && stack.hasTagCompound() && stack.getTagCompound().hasKey(AgriNBT.MATERIAL) && stack.getTagCompound().hasKey(AgriNBT.MATERIAL_META)) {
            NBTTagCompound tag = stack.getTagCompound();
            String name = tag.getString(AgriNBT.MATERIAL);
            int meta = tag.getInteger(AgriNBT.MATERIAL_META);
            material = new ItemStack(Block.getBlockFromName(name), 1, meta);
        } else {
            material = new ItemStack(Blocks.PLANKS);
        }
        list.add(AgriCore.getTranslator().translate("agricraft_tooltip.material") + ": " + material.getItem().getItemStackDisplayName(material));
    }

    /**
     * Retrieves the item's unlocalized name. This is the key used in the
     * language files. Should return something like
     * tile.agricraft:[internalname].[META].name Final as to prevent being
     * messed up.
     *
     * @param stack the item in question.
     * @return
     */
    @Override
    public final String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName() + "." + stack.getItemDamage();
    }

    /**
     * Retrieves metadata, returns what is passed.
     * @param meta
     * @return
     */
    @Override
    public int getMetadata(int meta) {
        return meta;
    }
}
