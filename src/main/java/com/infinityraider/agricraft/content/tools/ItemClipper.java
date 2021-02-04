package com.infinityraider.agricraft.content.tools;

import com.google.common.collect.Lists;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.event.AgriCropEvent;
import com.infinityraider.agricraft.api.v1.items.IAgriClipperItem;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemClipper extends ItemBase implements IAgriClipperItem {
    public ItemClipper() {
        super(Names.Items.CLIPPER, new Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
                .maxStackSize(1)
        );
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(@Nonnull ItemUseContext context) {
        World world = context.getWorld();
        if(world.isRemote()) {
            return ActionResultType.PASS;
        }
        BlockPos pos = context.getPos();
        ItemStack stack = context.getItem();
        PlayerEntity player = context.getPlayer();
        return AgriApi.getCrop(world, pos).map(crop -> {
            if(!crop.hasPlant()) {
                return ActionResultType.FAIL;
            }
            if(!crop.getPlant().allowsClipping(crop.getGrowthStage(), stack, player)) {
                return ActionResultType.FAIL;
            }
            if(MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Clip.Pre(crop, stack, player))) {
                return ActionResultType.FAIL;
            }
            List<ItemStack> drops = Lists.newArrayList();
            crop.getPlant().getClipProducts(drops::add, stack, crop.getGrowthStage(), crop.getStats(), world.getRandom());
            crop.setGrowthStage(crop.getPlant().getInitialGrowthStage());
            crop.getPlant().onClipped(crop, stack, player);
            AgriCropEvent.Clip.Post event = new AgriCropEvent.Clip.Post(crop, stack, drops, player);
            MinecraftForge.EVENT_BUS.post(event);
            event.getDrops().forEach(crop::dropItem);
            return ActionResultType.SUCCESS;
        }).orElse(ActionResultType.FAIL);
    }
}
