package com.InfinityRaider.AgriCraft.items.blocks;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.NBTHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * The root item for all CustomWood blocks.
 */
public class ItemBlockCustomWood extends ItemBlockAgricraft {
	
    /**
     * The default constructor.
     * A super call to this is generally all that is needed in subclasses.
     * 
     * @param block the block associated with this item.
     */
    public ItemBlockCustomWood(Block block) {
        super(block);
        this.setHasSubtypes(true);
    }

    /**
     * Populates the sub-item list.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        this.getSubItems(list);
    }

    /**
     * Populates the sub-item list.
     * This method allows getting sub blocks server side as well (no @side, like {@link #getSubItems(Item, CreativeTabs, List)}). 
     * This method is marked for cleaning.
     * 
     * @param list the list to populate.
     */
    public void getSubItems(List list) {
        ArrayList<ItemStack> registeredMaterials = new ArrayList<ItemStack>();
        ArrayList<ItemStack> planks = OreDictionary.getOres(Names.OreDict.plankWood);
        for(ItemStack plank:planks) {
            if(plank.getItem() instanceof ItemBlock) {
                // Skip the ExU stuff for now as we don't support its textures yet
                // TODO: Find out how ExU generates the colored textures and integrate it
                if (Loader.isModLoaded(Names.Mods.extraUtilities) && ((ItemBlock) plank.getItem()).field_150939_a.getClass().getSimpleName().equals("BlockColor"))
                    continue;

                if (plank.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                    ArrayList<ItemStack> subItems = new ArrayList<ItemStack>();
                    Side side = FMLCommonHandler.instance().getEffectiveSide();
                    if(side==Side.CLIENT) {
                        plank.getItem().getSubItems(plank.getItem(), null, subItems);
                    }
                    else {
                        for(int i=0;i<16;i++) {
                            //on the server register every meta as a recipe. The client won't know of this, so it's perfectly ok (don't tell anyone)
                            subItems.add(new ItemStack(plank.getItem(), 1, i));
                        }
                    }
                    for (ItemStack subItem : subItems) {
                        this.addMaterialToList(subItem, list, 0, registeredMaterials);
                    }
                } else {
                    this.addMaterialToList(plank, list, 0, registeredMaterials);
                }
            }
        }
    }

    /**
     * Determines if a list of materials (item stacks) has a material.
     * 
     * @param registeredMaterials The list of materials to check in.
     * @param material the material to check for.
     * @return if the list has the material.
     */
    private static boolean hasMaterial(List<ItemStack> registeredMaterials, ItemStack material) {
        for(ItemStack stack:registeredMaterials) {
            if(material.getItem()==stack.getItem() && material.getItemDamage()==stack.getItemDamage()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a material (item stack) to a list if it's not registered in a list already.
     * 
     * @param stack the material to add.
     * @param list the list to add to.
     * @param objectMeta the material's meta value.
     * @param registeredMaterials the list of materials to check against.
     */
    @SuppressWarnings("unchecked")
    private void addMaterialToList(ItemStack stack, List list, int objectMeta, ArrayList<ItemStack> registeredMaterials) {
        if(!hasMaterial(registeredMaterials, stack)) {
            ItemStack entry = new ItemStack(this.field_150939_a, 1, objectMeta);
            NBTTagCompound tag = NBTHelper.getMaterialTag(stack);
            if (tag != null) {
                entry.setTagCompound(tag);
                list.add(entry);
                registeredMaterials.add(stack);
            }
        }
    }

    /**
     * Retrieves the block's displayable information.
     * This method does not need to be overridden by most CustomWood blocks.
     * <p>
     * If the block name is not displaying correctly, check the language files and Names.Objects.[blockname].
     * If that does not correct the issue, ensure that the block overrides both getInternalName() and getTileEntityName() and returns Names.Objects.[blockname].
     * </p>
     * <p>
     * All custom wood blocks have a material that we want shown, so we make this method final.
     * Some however, has more information they want to add, so we add a addMore() method to override in that event.
     * </p>
     */
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
        ItemStack material;
        if(stack.getItemDamage()==0 && stack.hasTagCompound() && stack.getTagCompound().hasKey(Names.NBT.material) && stack.getTagCompound().hasKey(Names.NBT.materialMeta)) {
            NBTTagCompound tag = stack.getTagCompound();
            String name = tag.getString(Names.NBT.material);
            int meta = tag.getInteger(Names.NBT.materialMeta);
            material = new ItemStack((Block) Block.blockRegistry.getObject(name), 1, meta);
        } else {
            material = new ItemStack(Blocks.planks);
        }
        list.add(StatCollector.translateToLocal("agricraft_tooltip.material")+": "+ material.getItem().getItemStackDisplayName(material));
    }

    /**
     * Retrieves the item's unlocalized name.
     * This is the key used in the language files.
     * Should return something like tile.agricraft:[internalname].[meta].name
     * Final as to prevent being messed up.
     * 
     * @param stack the item in question.
     */
    @Override
    public final String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName()+"."+stack.getItemDamage();
    }

    /**
     * Retrieves metadata, returns what is passed.
     */
    @Override
    public int getMetadata(int meta) {
        return meta;
    }
}