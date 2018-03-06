package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.tiles.TileEntityCustomWood;
import com.infinityraider.agricraft.utility.CustomWoodTypeRegistry;
import com.infinityraider.infinitylib.block.BlockTileCustomRenderedBase;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockCustomWood<T extends TileEntityCustomWood> extends BlockTileCustomRenderedBase<T> {

    public BlockCustomWood(String internalName) {
        super(internalName, Material.WOOD);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        setHarvestLevel("axe", 0);
        this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
        this.setSoundType(SoundType.WOOD);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCustomWood) {
            TileEntityCustomWood tileEntity = (TileEntityCustomWood) te;
            tileEntity.setMaterial(stack);
        }
        super.onBlockPlacedBy(world, pos, state, placer, stack);
    }

    //override this to delay the removal of the tile entity until after harvestBlock() has been called
    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return !player.capabilities.isCreativeMode || super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    //when the block is harvested
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        if ((!world.isRemote) && (!player.isSneaking())) {
            if (!player.capabilities.isCreativeMode) {       //drop items if the player is not in creative
                this.dropBlockAsItem(world, pos, state, 0);
            }
            world.setBlockToAir(pos);
        }
    }

    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        if (!world.isRemote) {
            List<ItemStack> drops = this.getDrops(world, pos, state, fortune);
            for (ItemStack drop : drops) {
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
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        ItemStack stack = new ItemStack(this, 1, state.getBlock().getMetaFromState(state));
        this.setTag(world, pos, stack);
        return stack;
    }

    //prevent block from being removed by leaves
    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    protected void setTag(IBlockAccess world, BlockPos pos, ItemStack stack) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityCustomWood) {
            TileEntityCustomWood tile = (TileEntityCustomWood) te;
            stack.setTagCompound(tile.getMaterialTag());
        }
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }           //tells minecraft that this is not a block (no levers can be placed on it, it's transparent, ...)

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getBlockModelResourceLocation() {
        return new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + getInternalName());
    }

    @Override
    public List<String> getOreTags() {
        return Collections.emptyList();
    }

    @Override
    protected InfinityProperty[] getPropertyArray() {
        return new InfinityProperty[]{};
    }

    @Override
    public final IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        Optional<T> tile = (Optional<T>) WorldHelper.getTile(world, pos, TileEntityCustomWood.class);
        return getExtendedCustomWoodState((IExtendedBlockState) state, tile)
                .withProperty(AgriProperties.CUSTOM_WOOD_TYPE, tile.map(TileEntityCustomWood::getMaterial).orElse(CustomWoodTypeRegistry.DEFAULT));
    }

    protected IExtendedBlockState getExtendedCustomWoodState(IExtendedBlockState state, Optional<T> tile) {
        return state;
    }

    @Override
    public final IUnlistedProperty[] getUnlistedPropertyArray() {
        final List<IUnlistedProperty> list = new ArrayList<>();
        this.addUnlistedProperties(list::add);
        list.add(AgriProperties.CUSTOM_WOOD_TYPE);
        return list.toArray(new IUnlistedProperty[list.size()]);
    }

    protected void addUnlistedProperties(Consumer<IUnlistedProperty> consumer) {
        // Nothing to do here.
    }

}
