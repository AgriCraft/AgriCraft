package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.api.v2.ICrop;
import com.InfinityRaider.AgriCraft.api.v2.IRake;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.items.RenderItemBase;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.WeightedRandom;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Tool to uproot weeds.
 * Comes in a wooden and iron variant.
 */
public class ItemHandRake extends ItemAgricraft implements IRake{
    private static final int WOOD_VARIANT_META = 0;
    private static final int IRON_VARIANT_META = 1;
    private static final int[] dropChance = new int[] {10, 25};

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
        return false;
    }

    /**
     * Calculates the new weed growth age depending on the used tool variant
     * @return 0, if iron variant is used, otherwise a random value of the interval [0, currentWeedMeta]
     */
    private int calculateGrowthStage(int toolMeta, int currentWeedMeta, Random random) {
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

    @Override
    public boolean removeWeeds(ICrop crop, ItemStack rake) {
        World world = crop.getTileEntity().getWorldObj();
        int x = crop.getTileEntity().xCoord;
        int y = crop.getTileEntity().yCoord;
        int z = crop.getTileEntity().zCoord;
        if (crop.hasWeed()) {
            int weedGrowthStage = world.getBlockMetadata(x, y, z);
            int newWeedGrowthStage = calculateGrowthStage(rake.getItemDamage(), weedGrowthStage, world.rand);
            crop.updateWeed(newWeedGrowthStage);
            if(ConfigurationHandler.rakingDrops && !crop.hasWeed() && world.rand.nextInt(100)<dropChance[rake.getItemDamage()%dropChance.length]) {
                ItemStack drop = ItemDropRegistry.instance().getDrop(world.rand);
                if(drop != null && drop.getItem() != null) {
                    float f = 0.7F;
                    double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    EntityItem entityitem = new EntityItem(world, (double) x + d0, (double) y + d1, (double) z + d2, drop);
                    entityitem.delayBeforeCanPickup = 10;
                    world.spawnEntityInWorld(entityitem);
                }
            }
            return true;
        }
        return false;
    }

    public static final class ItemDropRegistry {
        private static ItemDropRegistry INSTANCE;

        private final WeightedRandom<ItemStack> registry;

        static {
            instance().registerDrop(new ItemStack(Blocks.tallgrass, 1, 1), 20);
            instance().registerDrop(new ItemStack(Blocks.tallgrass, 1, 2), 10);
            instance().registerDrop(new ItemStack(Blocks.double_plant, 1, 2), 10);
        }

        private ItemDropRegistry() {
            registry = new WeightedRandom<ItemStack>();
        }

        public static ItemDropRegistry instance() {
            if(INSTANCE == null) {
                INSTANCE = new ItemDropRegistry();
            }
            return INSTANCE;
        }

        public void registerDrop(ItemStack stack, int weight) {
            registry.addEntry(stack, weight);
        }

        public void removeDrop(ItemStack stack) {
            registry.removeEntry(stack);
        }

        public ItemStack getDrop(Random rand) {
            return registry.getRandomEntry(rand).copy();
        }

        public int getWeight(ItemStack stack) {
            return registry.getWeight(stack);
        }
    }
}
