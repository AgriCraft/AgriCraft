package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.items.blocks.ItemBlockCustomWood;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.TextureCache;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityBase;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;

import com.InfinityRaider.AgriCraft.api.v1.IIconRegistrar;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public abstract class BlockCustomWood extends BlockContainerBase {
    @SideOnly(Side.CLIENT)
    private static TextureAtlasSprite defaultIcon;

    public BlockCustomWood() {
        super(Material.wood);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        setHarvestLevel("axe", 0);
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
        this.setStepSound(soundTypeWood);
    }

    public static List<ItemStack> getWoodTypes() {
        List<ItemStack> list = new ArrayList<>();
        List<ItemStack> planks = OreDictionary.getOres(Names.OreDict.plankWood);
        for(ItemStack plank:planks) {
            if(plank.getItem() instanceof ItemBlock) {
                // Skip the ExU stuff for now as we don't support its textures yet
                // TODO: Find out how ExU generates the colored textures and integrate it
                if (Loader.isModLoaded(Names.Mods.extraUtilities) && ((ItemBlock) plank.getItem()).block.getClass().getSimpleName().equals("BlockColor")) {
                    continue;
                }
                if (plank.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                    List<ItemStack> subItems = new ArrayList<>();
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
                    list.addAll(subItems);
                } else {
                    list.add(plank);
                }
            }
        }
        return list;
    }
    
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity te = world.getTileEntity(pos);
        if(te != null && te instanceof TileEntityCustomWood) {
            TileEntityCustomWood tileEntity = (TileEntityCustomWood) te;
            tileEntity.setMaterial(stack);
        }
    	super.onBlockPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
    }

    //override this to delay the removal of the tile entity until after harvestBlock() has been called
    @Override
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return !player.capabilities.isCreativeMode || super.removedByPlayer(world, pos, player, willHarvest);
    }

    //when the block is harvested
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
        if((!world.isRemote) && (!player.isSneaking())) {
            if(!player.capabilities.isCreativeMode) {       //drop items if the player is not in creative
                this.dropBlockAsItem(world,pos, state, 0);
            }
            world.setBlockToAir(pos);
        }
    }

    @Override
     public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        if(!world.isRemote) {
            List<ItemStack> drops = this.getDrops(world, pos, state, fortune);
            for(ItemStack drop:drops) {
                spawnAsEntity(world, pos, drop);
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        ItemStack drop = new ItemStack(this, 1);
        this.setTag(world, pos, drop);
        drops.add(drop);
        return drops;
    }

    //creative item picking
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
        IBlockState state = world.getBlockState(pos);
        ItemStack stack = new ItemStack(this, 1, state.getBlock().getDamageValue(world, pos));
        this.setTag(world, pos, stack);
        return stack;
    }

    //prevent block from being removed by leaves
    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, BlockPos pos) {
        return false;
    }

    protected void setTag(IBlockAccess world, BlockPos pos, ItemStack stack) {
        TileEntity te = world.getTileEntity(pos);
        if(te != null && te instanceof TileEntityCustomWood) {
            TileEntityCustomWood tile = (TileEntityCustomWood) te;
            stack.setTagCompound(tile.getMaterialTag());
        }
    }
    @Override
    public boolean isOpaqueCube() {return false;}           //tells minecraft that this is not a block (no levers can be placed on it, it's transparent, ...)

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {return false;}

    @Override
    protected Class<? extends ItemBlockCustomWood> getItemBlockClass() {
    	return ItemBlockCustomWood.class;
    }
    
    @Override
    public ItemStack getWailaStack(BlockBase block, TileEntityBase te) {
    	if(te != null && te instanceof TileEntityCustomWood) {
    		ItemStack stack = new ItemStack(block, 1, 0);
    		stack.setTagCompound(((TileEntityCustomWood) te).getMaterialTag());
    		return stack;
    	} else {
    		return null;
    	}
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getIcon() {
        return defaultIcon;

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegistrar iconRegistrar) {
        if(defaultIcon == null) {
            for(ItemStack stack : BlockCustomWood.getWoodTypes()) {
                Block block = ((ItemBlock) stack.getItem()).block;
                TextureCache.getInstance().retrieveIcons(block.getStateFromMeta(stack.getItemDamage()));
            }
        }
        defaultIcon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/oak_planks");
    }
}
