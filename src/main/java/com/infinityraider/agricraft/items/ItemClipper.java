package com.infinityraider.agricraft.items;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.items.IAgriClipperItem;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.infinitylib.item.IItemWithModel;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.utility.IRecipeRegister;
import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ItemClipper extends ItemBase implements IAgriClipperItem, IItemWithModel, IRecipeRegister {

    @AgriConfigurable(
            category = AgriConfigCategory.TOOLS,
            key = "Enable Clipper",
            comment = "Set to false to disable the Clipper."
    )
    public static boolean enableClipper = true;

    public ItemClipper() {
        super("clipper");
        this.setMaxStackSize(1);
        this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
    }

    @Override
    public boolean canItemEditBlocks() {
        return true;
    }

    //this is called when you right click with this item in hand
    @Override
    public EnumActionResult onItemUse(ItemStack item, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitx, float hity, float hitz) {
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof IAgriCrop) {
            IAgriCrop crop = (IAgriCrop) te;
            if (crop.hasSeed() && crop.getGrowthStage() > 1) {
                crop.setGrowthStage(crop.getGrowthStage() - 1);
                AgriSeed seed = crop.getSeed();
                seed = seed.withStat(seed.getStat());
                world.spawnEntityInWorld(new EntityItem(world, pos.getX(), pos.getY() + 1, pos.getZ(), ItemClipping.getClipping(seed, 1)));
                return EnumActionResult.SUCCESS;
            }
            return EnumActionResult.FAIL;
        }
        return EnumActionResult.PASS;   //return PASS or else no other use methods will be called (for instance "onBlockActivated" on the crops block)
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        // Nothing to see here...
    }

    @Override
    public boolean isEnabled() {
        return enableClipper;
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(new ShapedOreRecipe(this, " i ", "scn", " s ", 'i', "ingotIron", 's', "stickWood", 'c', Items.SHEARS));
    }

}
