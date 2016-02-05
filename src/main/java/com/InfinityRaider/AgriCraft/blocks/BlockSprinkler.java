package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderSprinkler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityBase;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntitySprinkler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockSprinkler extends BlockTileBase {
	
    public BlockSprinkler() {
    	super(Material.iron, Names.Objects.sprinkler, false);
    	this.setCreativeTab(AgriCraftTab.agriCraftTab);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        setHarvestLevel("axe", 0);
        this.maxX = Constants.UNIT * Constants.THREE_QUARTER;
        this.minX = Constants.UNIT * Constants.QUARTER;
        this.maxZ = this.maxX;
        this.minZ = this.minX;
        this.maxY = Constants.UNIT * (Constants.WHOLE + Constants.QUARTER);
        this.minY = Constants.UNIT * Constants.THREE_QUARTER;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySprinkler();
    }

    @Override
    public boolean isReplaceable(World world, BlockPos pos) {
        return false;
    }

    //prevent block from being removed by leaves
    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
        if((!world.isRemote) && (!player.isSneaking())) {
            if(!player.capabilities.isCreativeMode) {       //drop items if the player is not in creative
                this.dropBlockAsItem(world, pos, state, 0);
            }
            world.setBlockToAir(pos);
            world.removeTileEntity(pos);
        }
    }

    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float f, int i) {
        if(!world.isRemote) {
            ItemStack drop = new ItemStack(this, 1);
            spawnAsEntity(world, pos, drop);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
        //check if crops can stay
        if(!this.canBlockStay(world, pos)) {
            //the crop will be destroyed
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
            world.removeTileEntity(pos);
        }
    }

    //see if the block can stay
    public boolean canBlockStay(World world, BlockPos pos) {
        return (world.getBlockState(pos.add(0, 1, 0)).getBlock() instanceof BlockWaterChannel);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, BlockPos pos) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public boolean isOpaqueCube() {return false;}

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {return false;}

    @Override
    protected IProperty[] getPropertyArray() {
        return new IProperty[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return new RenderSprinkler();
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
    	return world.getBlockState(pos.add(0, 1, 0)).getBlock() instanceof BlockWaterChannel && world.getBlockState(pos).getBlock().getMaterial()== Material.air;
    }
    
    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
    	return null;
    }

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getIcon(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side, @Nullable TileEntityBase te) {
        TileEntity channel = world.getTileEntity(pos.add(0, 1, 0));
        if(channel != null && channel instanceof TileEntityChannel) {
            return ((TileEntityChannel) channel).getTexture(state, side);
        }
        return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
    }
    
}
