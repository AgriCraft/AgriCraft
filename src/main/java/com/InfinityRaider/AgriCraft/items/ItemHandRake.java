package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.items.RenderItemBase;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Tool to uproot weeds.
 * Comes in a wooden and iron variant.
 */
public class ItemHandRake extends ItemAgricraft {
    private static final int WOOD_VARIANT_META = 0;
    private static final int IRON_VARIANT_META = 1;
    private static final Random random = new Random();

    private final IIcon[] icons = new IIcon[2];

    public ItemHandRake() {
        super();
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.handRake;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return false;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if (te == null || !(te instanceof TileEntityCrop)) {
            return false;
        }
        TileEntityCrop crop = (TileEntityCrop) te;
        if (crop.hasWeed()) {
            int weedGrowthStage = world.getBlockMetadata(x, y, z);
            int newWeedGrowthStage = calculateGrowthStage(stack.getItemDamage(), weedGrowthStage);
            crop.updateWeed(newWeedGrowthStage);
            return true;
        }
        else if (crop.hasPlant()) {
            ((BlockCrop) world.getBlock(x, y, z)).canUproot(world, x, y, z);
        }
        return true;
    }

    /**
     * Calculates the new weed growth age depending on the used tool variant
     * @return 0, if iron variant is used, otherwise a random value of the interval [0, currentWeedMeta]
     */
    private int calculateGrowthStage(int toolMeta, int currentWeedMeta) {
        if (toolMeta == IRON_VARIANT_META) {
            return 0;
        }

        return Math.max(random.nextInt(currentWeedMeta/2+1)-1, 0)+currentWeedMeta/2;
    }

    @Override
    @SuppressWarnings("unchecked")
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

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
        list.add(StatCollector.translateToLocal("agricraft_tooltip.handRake"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        LogHelper.debug("registering icon for: " + this.getUnlocalizedName());
        icons[0] = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.')+1)+"_wood");
        icons[1] = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.')+1)+"_iron");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderItemBase getItemRenderer() {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        if(meta<=1) {
            return this.icons[meta];
        }
        return null;
    }
}
