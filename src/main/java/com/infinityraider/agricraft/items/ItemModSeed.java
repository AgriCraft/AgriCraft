package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.api.v1.IAgriCraftSeed;
import com.infinityraider.agricraft.api.v1.IIconRegistrar;
import com.infinityraider.agricraft.api.v1.IMutation;
import com.infinityraider.agricraft.blocks.BlockModPlant;
import com.infinityraider.agricraft.creativetab.AgriCraftTab;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.handler.config.MutationConfig;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.renderers.items.RenderableItemRenderer;
import com.infinityraider.agricraft.renderers.renderinghacks.BlockRendererDispatcherWrapped;
import com.infinityraider.agricraft.utility.LogHelper;
import com.infinityraider.agricraft.utility.RegisterHelper;
import com.infinityraider.agricraft.utility.icon.IconUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class ItemModSeed extends ItemSeeds implements IAgriCraftSeed {
	
    @SideOnly(Side.CLIENT)
    private String information;

    /** This constructor shouldn't be called from anywhere except from the BlockModPlant public constructor, if you create a new BlockModPlant, its contructor will create the seed for you */
    public ItemModSeed(BlockModPlant plant, String information) {
        super(plant, plant.getGrowthRequirement().getSoil()==null?net.minecraft.init.Blocks.farmland:plant.getGrowthRequirement().getSoil().getBlock());
        if(FMLCommonHandler.instance().getEffectiveSide()==Side.CLIENT) {
            this.information = information;
        }
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
        //register seed
        RegisterHelper.registerSeed(this, plant);
    }
	
	@SideOnly(Side.CLIENT)
    public void registerItemRenderer() {
        BlockRendererDispatcherWrapped.getInstance().registerItemRenderingHandler(this, RenderableItemRenderer.getInstance());
    }

    @Override
    public List<IMutation> getMutations() {
        List<IMutation> list = new ArrayList<>();
        list.add(new Mutation(new ItemStack(this), new ItemStack(Items.pumpkin_seeds), new ItemStack(Items.wheat_seeds)));
        IMutation mutation = MutationConfig.getInstance().getDefaultMutation(new ItemStack(this));
        if(mutation != null) {
            mutation.setChance(((double) tier())/100.0D);
            list.add(mutation);
        }
        return list;
    }

    public BlockModPlant getPlant() {
        return (BlockModPlant) (this.getPlant(null, null).getBlock());
    }

    @Override
    public int tier() {
        return (this.getPlant()).tier;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        return this.information;
    }

    @SideOnly(Side.CLIENT)
    public void setInformation(String information) {
        this.information = information;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.getBlockState(pos).getBlock() == AgriCraftBlocks.blockCrop) {
            LogHelper.debug("Trying to plant seed " + stack.getItem().getUnlocalizedName() + " on crops");
            return true;
        }
        if (CropPlantHandler.getGrowthRequirement(stack.getItem(), stack.getItemDamage()).isValidSoil(world, pos)) {
            BlockPos blockPosUp = pos.add(0, 1, 0);
            if (side != EnumFacing.UP) {
                return false;
            } else if (player.canPlayerEdit(pos, side, stack) && player.canPlayerEdit(blockPosUp, side, stack)) {
                if (world.isAirBlock(blockPosUp)) {
                    world.setBlockState(blockPosUp, this.getPlant().getStateFromMeta(0), 3);
                    --stack.stackSize;
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public TextureAtlasSprite getIcon() {
		// Maybye not the most efficient...
        return IconUtil.getIcon(this);
    }

    @Override
    public void registerIcons(IIconRegistrar iconRegistrar) {
        String name = this.getUnlocalizedName();
        int index = name.indexOf(":");
        name = index > 0 ? name.substring(index+1) : name;
        index = name.indexOf(".");
        name = index > 0 ? name.substring(index+1) : name;
        iconRegistrar.registerIcon("agricraft:items/"+name);
    }
	
}
