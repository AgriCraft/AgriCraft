package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.container.ContainerSeedAnalyzer;
import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.handler.GuiHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;

public class BlockSeedAnalyzer extends Block implements ITileEntityProvider {
    public BlockSeedAnalyzer() {
        super(Material.ground);
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
        this.isBlockContainer = true;
        this.setTickRandomly(false);
        //set mining statistics
        this.setHardness(1);
        this.setResistance(1);
        //set the bounding box dimensions
        this.maxX = 15* Constants.unit;
        this.minX = 1*Constants.unit;
        this.maxZ = this.maxX;
        this.minZ = this.minX;
        this.maxY = 4*Constants.unit;
        this.minY = 0;
    }

    //creates a new tile entity every time a block of this type is placed
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySeedAnalyzer();
    }

    //this sets the block's orientation based upon the direction the player is looking when the block is placed
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        if(world.getTileEntity(x, y, z) instanceof TileEntitySeedAnalyzer) {
            TileEntitySeedAnalyzer analyzer = (TileEntitySeedAnalyzer) world.getTileEntity(x, y, z);
            int direction = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
            switch(direction) {
                case 0: analyzer.setDirection(ForgeDirection.NORTH.ordinal()); break;
                case 1: analyzer.setDirection(ForgeDirection.EAST.ordinal()); break;
                case 2: analyzer.setDirection(ForgeDirection.SOUTH.ordinal()); break;
                case 3: analyzer.setDirection(ForgeDirection.WEST.ordinal()); break;
            }
        }
    }

    //called when the block is broken
     @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
         if(!world.isRemote) {
             world.removeTileEntity(x, y, z);
             world.setBlockToAir(x, y, z);
         }
    }

    //override this to delay the removal of the tile entity until after harvestBlock() has been called
    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        return !player.capabilities.isCreativeMode || super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    //this gets called when the block is mined
    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        if(!world.isRemote) {
            if(!player.capabilities.isCreativeMode) {
                this.dropBlockAsItem(world, x, y, z, meta, 0);
            }
            this.breakBlock(world, x, y, z, world.getBlock(x, y, z), meta);
        }
    }

    //get a list with items dropped by the the crop
    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        items.add(new ItemStack(Item.getItemFromBlock(Blocks.blockSeedAnalyzer), 1, 0));
        if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntitySeedAnalyzer) {
            TileEntitySeedAnalyzer analyzer = (TileEntitySeedAnalyzer) world.getTileEntity(x, y, z);
            if(analyzer.getStackInSlot(ContainerSeedAnalyzer.seedSlotId)!=null) {
                items.add(analyzer.getStackInSlot(ContainerSeedAnalyzer.seedSlotId));
            }
            if(analyzer.getStackInSlot(ContainerSeedAnalyzer.journalSlotId)!=null) {
                items.add(analyzer.getStackInSlot(ContainerSeedAnalyzer.journalSlotId));
            }
        }
        return items;
    }

    //open the gui when the block is activated
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fX, float fY, float fZ) {
        if(player.isSneaking()) {
            return false;
        }
        if(!world.isRemote) {
            player.openGui(AgriCraft.instance, GuiHandler.seedAnalyzerID, world, x, y, z);
        }
        return true;
    }

    //rendering stuff
    @Override
    public int getRenderType() {return -1;}                 //get default render type: net.minecraft.client.renderer
    @Override
    public boolean isOpaqueCube() {return false;}           //tells minecraft that this is not a block (no levers can be placed on it, it's transparent, ...)
    @Override
    public boolean renderAsNormalBlock() {return false;}    //tells minecraft that this has custom rendering
    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int i) {return false;}
    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {return false;}        //no particles when this block gets hit
    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {return false;}     //no particles when destroyed
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.') + 1));
    }
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return net.minecraft.init.Blocks.planks.getIcon(0, 0);
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int id, int data) {
        super.onBlockEventReceived(world, x, y, z, id, data);
        TileEntity tileEntity = world.getTileEntity(x,y,z);
        return (tileEntity!=null)&&(tileEntity.receiveClientEvent(id,data));
    }
}
