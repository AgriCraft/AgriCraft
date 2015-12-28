package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.container.ContainerSeedAnalyzer;
import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.handler.GuiHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderSeedAnalyzer;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import com.InfinityRaider.AgriCraft.utility.icon.IIconRegistrar;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class BlockSeedAnalyzer extends BlockContainerBase {
    public BlockSeedAnalyzer() {
        super(Material.ground);
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
        this.isBlockContainer = true;
        this.setTickRandomly(false);
        //set mining statistics
        this.setHardness(1);
        this.setResistance(1);
        //set the bounding box dimensions
        this.maxX = Constants.UNIT * (Constants.WHOLE - 1);
        this.minX = Constants.UNIT;
        this.maxZ = this.maxX;
        this.minZ = this.minX;
        this.maxY = Constants.UNIT * Constants.QUARTER;
        this.minY = 0;
    }

    //creates a new tile entity every time a block of this type is placed
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySeedAnalyzer();
    }

    @Override
    protected String getTileEntityName() {
        return Names.Objects.seedAnalyzer;
    }

    //called when the block is broken
     @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
         if(!world.isRemote) {
             world.removeTileEntity(pos);
             world.setBlockToAir(pos);
         }
    }

    //override this to delay the removal of the tile entity until after harvestBlock() has been called
    @Override
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return !player.capabilities.isCreativeMode || super.removedByPlayer(world, pos, player, willHarvest);
    }

    //this gets called when the block is mined
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
        if(!world.isRemote) {
            if(!player.capabilities.isCreativeMode) {
                this.dropBlockAsItem(world, pos, state, 0);
            }
            this.breakBlock(world, pos, state);
        }
    }

    //get a list with items dropped by the the crop
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> items = new ArrayList<>();
        items.add(new ItemStack(Item.getItemFromBlock(Blocks.blockSeedAnalyzer), 1, 0));
        if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntitySeedAnalyzer) {
            TileEntitySeedAnalyzer analyzer = (TileEntitySeedAnalyzer) world.getTileEntity(pos);
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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float fX, float fY, float fZ) {
        if(player.isSneaking()) {
            return false;
        }
        if(!world.isRemote) {
            player.openGui(AgriCraft.instance, GuiHandler.seedAnalyzerID, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    //rendering stuff
    @Override
    public boolean isOpaqueCube() {return false;}

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {return false;}

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {return false;}        //no particles when this block gets hit

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer) {return false;}     //no particles when destroyed

    @Override
    public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int id, int data) {
        super.onBlockEventReceived(world,pos, state, id, data);
        TileEntity tileEntity = world.getTileEntity(pos);
        return (tileEntity!=null)&&(tileEntity.receiveClientEvent(id,data));
    }

    @Override
    public boolean isMultiBlock() {
        return false;
    }

    @Override
    protected IProperty[] getPropertyArray() {
        return new IProperty[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return new RenderSeedAnalyzer();
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return null;
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.seedAnalyzer;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getIcon() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/oak_planks");
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegistrar iconRegistrar) {}
}
