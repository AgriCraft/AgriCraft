package com.infinityraider.agricraft.blocks.irrigation;

import com.infinityraider.agricraft.api.v1.misc.IAgriConnectable;
import com.infinityraider.agricraft.api.v1.util.AgriSideMetaMatrix;
import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.items.blocks.ItemBlockCustomWood;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.renderers.blocks.RenderTank;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityTank;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWaterTank extends BlockCustomWood<TileEntityTank> {

    private final ItemBlockCustomWood itemBlock;

    public BlockWaterTank() {
        super("water_tank");
        this.setTickRandomly(false);
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
        FluidUtil.interactWithFluidHandler(player, hand, world, pos, side);
        // Figure out if this is a fluid thing or not.
        final ItemStack stack = player.getHeldItem(hand);
        final FluidStack fluid = FluidUtil.getFluidContained(stack);
        // Return depending if holding a fluid stack to prevent accidental water placement.
        return (fluid != null);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        // Call supermethod.
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        // Update tile.
        WorldHelper.getTile(world, pos, IAgriConnectable.class)
                .ifPresent(IAgriConnectable::refreshConnections);
        // Update neighbors.
        WorldHelper.getTileNeighbors(world, pos, IAgriConnectable.class)
                .forEach(IAgriConnectable::refreshConnections);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        WorldHelper.getTile(world, pos, IAgriConnectable.class)
                .ifPresent(IAgriConnectable::refreshConnections);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        // Call Super Method.
        super.breakBlock(world, pos, state);

        // Notify neighbors.
        WorldHelper.getTileNeighbors(world, pos, IAgriConnectable.class)
                .forEach(IAgriConnectable::refreshConnections);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderTank getRenderer() {
        return new RenderTank(this);
    }

    @Override
    protected void addUnlistedProperties(Consumer<IUnlistedProperty> consumer) {
        super.addUnlistedProperties(consumer);
        AgriSideMetaMatrix.addUnlistedProperties(consumer);
    }

    @Override
    protected IExtendedBlockState getExtendedCustomWoodState(IExtendedBlockState state, Optional<TileEntityTank> tile) {
        return tile.map(IAgriConnectable::getConnections).orElseGet(AgriSideMetaMatrix::new).writeToBlockState(state);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public boolean isEnabled() {
        return AgriCraftConfig.enableIrrigation;
    }
    
}
