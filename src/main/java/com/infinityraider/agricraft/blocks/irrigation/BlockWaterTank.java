package com.infinityraider.agricraft.blocks.irrigation;

import com.infinityraider.agricraft.api.v1.irrigation.IrrigationConnection;
import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.items.blocks.ItemBlockCustomWood;
import com.infinityraider.agricraft.renderers.blocks.RenderTank;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityTank;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.Optional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWaterTank extends BlockCustomWood<TileEntityTank> {

    private final ItemBlockCustomWood itemBlock;

    public BlockWaterTank() {
        super("water_tank");
        this.itemBlock = new ItemBlockCustomWood(this);
    }

    @Override
    public Optional<ItemBlockCustomWood> getItemBlock() {
        return Optional.of(this.itemBlock);
    }

    @Override
    public TileEntityTank createNewTileEntity(World world, int meta) {
        return new TileEntityTank();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        // Attempt to interact with fluid stuff.
        WorldHelper.getTile(world, pos, TileEntityTank.class)
                .ifPresent(t -> FluidUtil.interactWithFluidHandler(player, hand, t));
        // Figure out if this is a fluid thing or not.
        final ItemStack stack = player.getHeldItem(hand);
        final FluidStack fluid = FluidUtil.getFluidContained(stack);
        // Return depending if holding a fluid stack to prevent accidental water placement.
        return (fluid != null);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderTank getRenderer() {
        return new RenderTank(this);
    }

    @Override
    protected InfinityProperty[] getPropertyArray() {
        return IrrigationConnection.CONNECTIONS;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        final Optional<TileEntityTank> tile = WorldHelper.getTile(world, pos, TileEntityTank.class);
        if (!tile.isPresent()) {
            return state;
        }
        tile.get().checkConnections();
        final IrrigationConnection sides = new IrrigationConnection();
        for (EnumFacing facing : EnumFacing.VALUES) {
            sides.set(facing, tile.get().getConnectionType(facing));
        }
        return sides.write(state);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

}
