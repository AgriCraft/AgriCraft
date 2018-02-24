package com.infinityraider.agricraft.items;

import com.agricraft.agricore.core.AgriCore;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.items.IAgriRakeItem;
import com.infinityraider.agricraft.api.v1.misc.IAgriRakeable;
import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.infinitylib.item.IItemWithModel;
import com.infinityraider.infinitylib.item.ItemBase;
import java.util.List;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Tool to uproot weeds. Comes in a wooden and iron variant.
 */
public class ItemRake extends ItemBase implements IAgriRakeItem, IItemWithModel {

    private static final int WOOD_VARIANT_META = 0;
    private static final int IRON_VARIANT_META = 1;

    public ItemRake() {
        super("rake");
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof IAgriRakeable) {
            IAgriRakeable tile = (IAgriRakeable) te;
            if (tile.onRaked(player)) {
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.FAIL;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (tab == this.getCreativeTab() || tab == CreativeTabs.SEARCH) {
            list.add(new ItemStack(this, 1, WOOD_VARIANT_META));
            list.add(new ItemStack(this, 1, IRON_VARIANT_META));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        String base = super.getUnlocalizedName(itemStack);
        switch (itemStack.getItemDamage()) {
            case WOOD_VARIANT_META:
                return base + ".wood";
            case IRON_VARIANT_META:
                return base + ".iron";
            default:
                throw new IllegalArgumentException("Unsupported meta value of " + itemStack.getItemDamage() + " for ItemRake.");
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(AgriCore.getTranslator().translate("agricraft_tooltip.rake"));
    }

    @Override
    public List<Tuple<Integer, ModelResourceLocation>> getModelDefinitions() {
        return ImmutableList.of(
                new Tuple<>(0, new ModelResourceLocation(this.getRegistryName() + "")),
                new Tuple<>(1, new ModelResourceLocation(this.getRegistryName() + "_iron"))
        );
    }

}
