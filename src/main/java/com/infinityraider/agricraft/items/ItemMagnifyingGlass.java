package com.infinityraider.agricraft.items;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.misc.IAgriDisplayable;
import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.infinitylib.item.IItemWithModel;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.utility.IRecipeRegister;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ItemMagnifyingGlass extends ItemBase implements IItemWithModel, IRecipeRegister {

    @AgriConfigurable(
            category = AgriConfigCategory.TOOLS,
            key = "Enable Magnifying Glass",
            comment = "Set to false to disable the Magnifying Glass."
    )
    public static boolean enableMagnifyingGlass = true;

    public ItemMagnifyingGlass() {
        super("magnifying_glass");
        this.setMaxStackSize(1);
        this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
    }

    //I'm overriding this just to be sure
    @Override
    public boolean canItemEditBlocks() {
        return false;
    }

    //this is called when you right click with this item in hand
    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (world.isRemote) {
            List<String> list = new ArrayList<>();
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            TileEntity te = world.getTileEntity(pos);

            // Add a separator.
            list.add("========== " + AgriCore.getTranslator().translate("item.agricraft:magnifying_glass.name") + " ==========");

            // Add lighting information.
            list.add("Brightness: (" + world.getLightFromNeighbors(pos.up()) + "/16)");

            // Add block information.
            if (block instanceof IAgriDisplayable) {
                ((IAgriDisplayable) block).addDisplayInfo(list::add);
            }

            // Add tile information.
            if (te instanceof IAgriDisplayable) {
                ((IAgriDisplayable) te).addDisplayInfo(list::add);
            }

            // Display information.
            for (String msg : list) {
                player.addChatComponentMessage(new TextComponentString(msg));
            }
        }
        return EnumActionResult.SUCCESS;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        list.add(AgriCore.getTranslator().translate("agricraft_tooltip.magnifyingGlass"));
    }

    @Override
    public boolean isEnabled() {
        return enableMagnifyingGlass;
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(new ShapedOreRecipe(this, "sgs", " s ", " s ", 's', "stickWood", 'g', "paneGlass"));
    }

}
