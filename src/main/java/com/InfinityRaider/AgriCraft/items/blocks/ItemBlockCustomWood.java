package com.infinityraider.agricraft.items.blocks;

import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.reference.AgriCraftMods;
import com.infinityraider.agricraft.utility.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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

    @SideOnly(Side.CLIENT)
    public static TextureAtlasSprite getTextureFromStack(ItemStack stack) {
        //TODO
        return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
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
        List<ItemStack> registeredMaterials = new ArrayList<>();
        List<ItemStack> planks = OreDictionary.getOres("plankWood");
        for(ItemStack plank:planks) {
            if(plank.getItem() instanceof ItemBlock) {
                // Skip the ExU stuff for now as we don't support its textures yet
                // TODO: Find out how ExU generates the colored textures and integrate it
                if (Loader.isModLoaded(AgriCraftMods.extraUtilities) && ((ItemBlock) plank.getItem()).block.getClass().getSimpleName().equals("BlockColor"))
                    continue;

                if (plank.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                    List<ItemStack> subItems = new ArrayList<>();
                    Side side = FMLCommonHandler.instance().getEffectiveSide();
                    if(side==Side.CLIENT) {
                        plank.getItem().getSubItems(plank.getItem(), null, subItems);
                    }
                    else {
                        for(int i=0;i<16;i++) {
                            //on the server register every META as a recipe. The client won't know of this, so it's perfectly ok (don't tell anyone)
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
     * Determines if a list of materials (item stacks) has a MATERIAL.
     * 
     * @param registeredMaterials The list of materials to check in.
     * @param material the MATERIAL to check for.
     * @return if the list has the MATERIAL.
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
     * Adds a MATERIAL (item stack) to a list if it's not registered in a list already.
     * 
     * @param stack the MATERIAL to add.
     * @param list the list to add to.
     * @param objectMeta the MATERIAL's META value.
     * @param registeredMaterials the list of materials to check against.
     */
    @SuppressWarnings("unchecked")
    private void addMaterialToList(ItemStack stack, List list, int objectMeta, List<ItemStack> registeredMaterials) {
        if(!hasMaterial(registeredMaterials, stack)) {
            ItemStack entry = new ItemStack(this.block, 1, objectMeta);
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
 All custom WOOD blocks have a MATERIAL that we want shown, so we make this method final.
 Some however, has more information they want to add, so we add a addMore() method to OVERRIDE in that event.
 </p>
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        ItemStack material;
        if(stack.getItemDamage()==0 && stack.hasTagCompound() && stack.getTagCompound().hasKey(AgriCraftNBT.MATERIAL) && stack.getTagCompound().hasKey(AgriCraftNBT.MATERIAL_META)) {
            NBTTagCompound tag = stack.getTagCompound();
            String name = tag.getString(AgriCraftNBT.MATERIAL);
            int meta = tag.getInteger(AgriCraftNBT.MATERIAL_META);
            material = new ItemStack(Block.getBlockFromName(name), 1, meta);
        } else {
            material = new ItemStack(Blocks.planks);
        }
        list.add(StatCollector.translateToLocal("agricraft_tooltip.material")+": "+ material.getItem().getItemStackDisplayName(material));
    }

    /**
     * Retrieves the item's unlocalized name.
     * This is the key used in the language files.
 Should return something like tile.agricraft:[internalname].[META].name
 Final as to prevent being messed up.
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