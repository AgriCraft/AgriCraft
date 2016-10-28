package com.infinityraider.agricraft.compat.computer.blocks;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.compat.computer.renderers.RenderPeripheral;
import com.infinityraider.agricraft.compat.computer.tiles.TileEntityPeripheral;
import com.infinityraider.agricraft.handler.GuiHandler;
import com.infinityraider.agricraft.network.MessagePeripheralCheckNeighbours;
import com.infinityraider.infinitylib.block.BlockTileCustomRenderedBase;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;

@Optional.Interface(modid = "ComputerCraft", iface = "dan200.computercraft.api.peripheral.IPeripheralProvider")
public class BlockPeripheral extends BlockTileCustomRenderedBase<TileEntityPeripheral> {
    public BlockPeripheral() {
        super("peripheral", Material.IRON);
    }

    @Override
    public TileEntityPeripheral createNewTileEntity(World world, int meta) {
        return new TileEntityPeripheral();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderPeripheral getRenderer() {
        return new RenderPeripheral(this);
    }

    @Override
    public ModelResourceLocation getBlockModelResourceLocation() {
        return null;
    }

    @Override
    public List<String> getOreTags() {
        return Collections.emptyList();
    }

    @Override
    protected InfinityProperty[] getPropertyArray() {
        return new InfinityProperty[0];
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return null;
    }

    /*
    @Override
    @Optional.Method(modid = Names.AgriCraftMods.computerCraft)
    public IPeripheral getPeripheral(World world, BlockPos pos, int side) {
        TileEntity te = world.getTileEntity(pos);
        if(te==null || !(te instanceof TileEntityPeripheral)) {
            return null;
        }
        return (TileEntityPeripheral) te;
    }
     */

    //called when the block is broken
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            world.removeTileEntity(pos);
            world.setBlockToAir(pos);
        }
    }

    //override this to delay the removal of the tile entity until after harvestBlock() has been called
    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return !player.capabilities.isCreativeMode || super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    //this gets called when the block is mined
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        if (!world.isRemote) {
            if (!player.capabilities.isCreativeMode) {
                this.dropBlockAsItem(world, pos, state, 0);
            }
            this.breakBlock(world, pos, state);
        }
    }

    //open the gui when the block is activated
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            return false;
        }
        if (!world.isRemote) {
            player.openGui(AgriCraft.instance, GuiHandler.PERIPHERAL_GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void onNeighborChange(IBlockAccess iba, BlockPos pos, BlockPos neighbor) {
        NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(iba.getWorldType().getWorldTypeID(), pos.getX(), pos.getY(), pos.getZ(), 32);
        AgriCraft.instance.getNetworkWrapper().sendToAllAround(new MessagePeripheralCheckNeighbours(pos), point);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }
}
