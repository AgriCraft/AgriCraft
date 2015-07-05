package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import com.InfinityRaider.AgriCraft.utility.NBTHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class ItemBlockCustomWood extends ItemBlock {
    public ItemBlockCustomWood(Block block) {
        super(block);
        this.setHasSubtypes(true);
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        NBTTagCompound tag = stack.stackTagCompound;
        if (!world.setBlock(x, y, z, field_150939_a, metadata, 3)) {
            return false;
        }
        if (world.getBlock(x, y, z) == field_150939_a) {
            field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
            field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
            if(world.getTileEntity(x, y, z)!=null && world.getTileEntity(x, y, z) instanceof TileEntityCustomWood) {
                TileEntityCustomWood tileEntity = (TileEntityCustomWood) world.getTileEntity(x, y, z);
                tileEntity.setMaterial(tag);
            }
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        this.getSubItems(list);
    }

    //create this method to allow getting sub blocks server side as well
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

    //checks if a list of materials (item stacks) has this material
    private boolean hasMaterial(ArrayList<ItemStack> registeredMaterials, ItemStack material) {
        for(ItemStack stack:registeredMaterials) {
            if(material.getItem()==stack.getItem() && material.getItemDamage()==stack.getItemDamage()) {
                return true;
            }
        }
        return false;
    }

    //adds a material (item stack) to a list if it's not registered in a list already
    private void addMaterialToList(ItemStack stack, List list, int objectMeta, ArrayList<ItemStack> registeredMaterials) {
        if(!this.hasMaterial(registeredMaterials, stack)) {
            ItemStack entry = new ItemStack(this.field_150939_a, 1, objectMeta);
            NBTTagCompound tag = NBTHelper.getMaterialTag(stack);
            if (tag != null) {
                entry.setTagCompound(tag);
                list.add(entry);
                registeredMaterials.add(stack);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
        if(stack.getItemDamage()==0 && stack.hasTagCompound() && stack.getTagCompound().hasKey(Names.NBT.material) && stack.getTagCompound().hasKey(Names.NBT.materialMeta)) {
            NBTTagCompound tag = stack.getTagCompound();
            String name = tag.getString(Names.NBT.material);
            int meta = tag.getInteger(Names.NBT.materialMeta);
            ItemStack material = new ItemStack((Block) Block.blockRegistry.getObject(name), 1, meta);
            list.add(StatCollector.translateToLocal("agricraft_tooltip.material")+": "+ material.getItem().getItemStackDisplayName(material));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName()+"."+stack.getItemDamage();
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }
}