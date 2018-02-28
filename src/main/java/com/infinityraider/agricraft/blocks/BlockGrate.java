package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.items.blocks.ItemBlockCustomWood;
import com.infinityraider.agricraft.items.blocks.ItemBlockGrate;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.renderers.blocks.RenderBlockGrate;
import com.infinityraider.agricraft.tiles.decoration.TileEntityGrate;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGrate extends BlockCustomWood<TileEntityGrate> {

    private final ItemBlockGrate itemBlock;

    public BlockGrate() {
        super("grate");
        this.fullBlock = false;
        this.itemBlock = new ItemBlockGrate(this);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    protected InfinityProperty[] getPropertyArray() {
        return new InfinityProperty[]{AgriProperties.AXIS, AgriProperties.OFFSET, AgriProperties.VINES};
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        Optional<TileEntityGrate> tile = WorldHelper.getTile(worldIn, pos, TileEntityGrate.class);
        state = AgriProperties.VINES.applyToBlockState(state, tile.map(TileEntityGrate::getVines).orElse(TileEntityGrate.EnumVines.NONE));
        state = AgriProperties.OFFSET.applyToBlockState(state, tile.map(TileEntityGrate::getOffset).orElse(TileEntityGrate.EnumOffset.NEAR));
        state = AgriProperties.AXIS.applyToBlockState(state, tile.map(TileEntityGrate::getAxis).orElse(EnumFacing.Axis.X));
        return state;
    }

    @Override
    public Class<? extends ItemBlockCustomWood> getItemBlockClass() {
        return ItemBlockGrate.class;
    }

    @Override
    public Optional<? extends ItemBlock> getItemBlock() {
        return Optional.of(this.itemBlock);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockGrate getRenderer() {
        return new RenderBlockGrate(this);
    }

    @Override
    public TileEntityGrate createNewTileEntity(World world, int meta) {
        return new TileEntityGrate();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null || !(tile instanceof TileEntityGrate)) {
            return true;
        }
        TileEntityGrate grate = (TileEntityGrate) tile;
        boolean front = grate.isPlayerInFront(player);
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            if (grate.removeVines(front)) {
                spawnAsEntity(world, pos, new ItemStack(Blocks.VINE, 1));
                return true;
            }
        } else if (stack.getItem() == Item.getItemFromBlock(Blocks.VINE)) {
            if (grate.addVines(front) && !player.capabilities.isCreativeMode) {
                stack.setCount(stack.getCount() - 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> items = super.getDrops(world, pos, state, fortune);
        TileEntity te = world.getTileEntity(pos);
        if (te != null && (te instanceof TileEntityGrate)) {
            TileEntityGrate grate = (TileEntityGrate) te;
            int stackSize = 0;
            stackSize = grate.hasVines(true) ? stackSize + 1 : stackSize;
            stackSize = grate.hasVines(false) ? stackSize + 1 : stackSize;
            if (stackSize > 0) {
                items.add(new ItemStack(Blocks.VINE, stackSize));
            }
        }
        return items;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        // Get Axis
        return WorldHelper.getTile(worldIn, pos, TileEntityGrate.class)
                .map(t -> t.getOffset().getBounds(t.getAxis()))
                .orElse(FULL_BLOCK_AABB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return getCollisionBoundingBox(state, worldIn, pos).offset(pos);
    }
    
    @Override
    public boolean isEnabled() {
        return AgriCraftConfig.enableGrates;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

}
