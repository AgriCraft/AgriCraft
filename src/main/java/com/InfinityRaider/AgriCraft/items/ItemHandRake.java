package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Tool to uproot weeds.
 * Comes in a wooden and iron variant.
 */
public class ItemHandRake extends Item {

    private static final int WOOD_VARIANT_META = 0;
    private static final int IRON_VARIANT_META = 1;
    private static final Random random = new Random();

    public ItemHandRake() {
        setCreativeTab(AgriCraftTab.agriCraftTab);
        maxStackSize = 1;
        hasSubtypes = true;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return false;
        }

        TileEntity te = world.getTileEntity(x, y, z);
        if (te == null || !(te instanceof TileEntityCrop)) {
            return true;
        }

        TileEntityCrop crop = (TileEntityCrop) te;
        if (!crop.weed) {
            return true;
        }

        int weedGrowthStage = world.getBlockMetadata(x, y, z);
        int newWeedGrowthStage = calculateGrowthStage(stack.getItemDamage(), weedGrowthStage);
        crop.updateWeed(newWeedGrowthStage);
        return true;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(item, 1, WOOD_VARIANT_META));
        list.add(new ItemStack(item, 1, IRON_VARIANT_META));
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        String base = super.getUnlocalizedName(itemStack);
        if (itemStack.getItemDamage() == WOOD_VARIANT_META) {
            return base + ".wood";
        } else if (itemStack.getItemDamage() == IRON_VARIANT_META) {
            return base + ".iron";
        } else {
            throw new IllegalArgumentException("Unsupported meta value of " + itemStack.getItemDamage() + " for ItemHandRake.");
        }
    }

    /**
     * Calculates the new weed growth age depending on the used tool variant
     * @return 0, if iron variant is used, otherwise a random value of the interval [0, currentWeedMeta]
     */
    private int calculateGrowthStage(int toolMeta, int currentWeedMeta) {
        if (toolMeta == IRON_VARIANT_META) {
            return 0;
        }

        return random.nextInt(currentWeedMeta + 1);
    }
}
