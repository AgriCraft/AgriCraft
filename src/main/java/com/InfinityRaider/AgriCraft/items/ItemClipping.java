package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.api.v1.ISeedStats;
import com.InfinityRaider.AgriCraft.farming.PlantStats;
import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.renderers.items.ClippingRenderer;
import com.InfinityRaider.AgriCraft.renderers.renderinghacks.BlockRendererDispatcherWrapped;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemClipping extends ItemBase {
	
    public ItemClipping() {
        super("clipping");
        this.setCreativeTab(null);
    }

	@Override
	public void registerItemRenderer() {
		BlockRendererDispatcherWrapped.getInstance().registerItemRenderingHandler(this, ClippingRenderer.getInstance());
	}

    @Override
    public boolean canItemEditBlocks() {return true;}

    //this is called when you right click with this item in hand
    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(world.isRemote) {
            return false;
        }
        if(stack==null || stack.getItem()==null || !stack.hasTagCompound()) {
            return false;
        }
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if(!(block instanceof BlockCrop)) {
            return false;
        }
        TileEntity te = world.getTileEntity(pos);
        if(te == null || !(te instanceof TileEntityCrop)) {
            return true;
        }
        TileEntityCrop crop = (TileEntityCrop) te;
        BlockCrop blockCrop = (BlockCrop) block;
        if(crop.isCrossCrop()) {
            blockCrop.harvest(world, pos, state, player, crop);
        }
        if(!crop.canPlant()) {
            return true;
        }
        ItemStack seed = ItemStack.loadItemStackFromNBT(stack.getTagCompound());
        ISeedStats stats = PlantStats.getStatsFromStack(seed);
        if(stats == null) {
            return false;
        }
        if(world.rand.nextInt(10)<=stats.getStrength()) {
            blockCrop.plantSeed(seed, world, pos);
        }
        stack.stackSize = stack.stackSize-1;
        return true;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String text = StatCollector.translateToLocal("item.agricraft:clipping.name");
        if(stack==null || !stack.hasTagCompound()) {
            return text;
        }
        ItemStack seed = ItemStack.loadItemStackFromNBT(stack.getTagCompound());
        if(seed==null || seed.getItem()==null || !CropPlantHandler.isValidSeed(seed)) {
            return text;
        }
        CropPlant plant = CropPlantHandler.getPlantFromStack(seed);
        if(plant == null) {
            return text;
        }
        ItemStack fruit = plant.getAllFruits().get(0);
        return fruit.getDisplayName() + " " + text;
    }
}
