package com.infinityraider.agricraft.content.tools;

import com.google.common.collect.Lists;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.event.AgriCropEvent;
import com.infinityraider.agricraft.api.v1.content.items.IAgriRakeItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemRake extends ItemBase implements IAgriRakeItem {
    public static RakeLogic WOOD_LOGIC = new RakeLogic(Names.Items.RAKE_WOOD) {
        @Override
        public boolean doRakeAction(IAgriCrop crop, ItemStack stack, @Nullable PlayerEntity player) {
            if(crop.world() == null || crop.world().isRemote()) {
                return false;
            }
            if(!crop.hasWeeds()) {
                return false;
            }
            IAgriWeed weeds = crop.getWeeds();
            IAgriGrowthStage current = crop.getWeedGrowthStage();
            IAgriGrowthStage previous = current.getPreviousStage(crop, crop.world().getRandom());
            if(current.equals(previous)) {
                return crop.removeWeed();
            } else {
                return crop.setWeed(weeds, previous);
            }
        }
    };

    public static RakeLogic IRON_LOGIC = new RakeLogic(Names.Items.RAKE_IRON) {
        @Override
        public boolean doRakeAction(IAgriCrop crop, ItemStack stack, @Nullable PlayerEntity player) {
            if(crop.world() == null || crop.world().isRemote()) {
                return false;
            }
            if(!crop.hasWeeds()) {
                return false;
            }
            return crop.removeWeed();
        }
    };

    private final RakeLogic logic;

    public ItemRake(RakeLogic logic) {
        super(logic.getName(), new Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
                .maxStackSize(1)
        );
        this.logic = logic;
    }

    protected RakeLogic getRakeLogic() {
        return this.logic;
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(@Nonnull ItemUseContext context) {
        return AgriApi.getCrop(context.getWorld(), context.getPos())
                .map(crop -> this.getRakeLogic().applyLogic(crop, context.getItem(), context.getPlayer()))
                .orElse(ActionResultType.FAIL);
    }

    public static abstract class RakeLogic {
        private final String name;

        public RakeLogic(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public final ActionResultType applyLogic(IAgriCrop crop, ItemStack stack, @Nullable PlayerEntity player) {
            if(crop.world() == null || crop.world().isRemote()) {
                return ActionResultType.PASS;
            }
            if(!MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Rake.Pre(crop, stack, player))) {
                IAgriWeed weeds = crop.getWeeds();
                IAgriGrowthStage stage = crop.getWeedGrowthStage();
                if(this.doRakeAction(crop, stack, player)) {
                    List<ItemStack> drops = Lists.newArrayList();
                    weeds.onRake(stage, drops::add, crop.world().getRandom(), player);
                    AgriCropEvent.Rake.Post event = new AgriCropEvent.Rake.Post(crop, stack, drops, player);
                    MinecraftForge.EVENT_BUS.post(event);
                    event.getDrops().forEach(crop::dropItem);
                    return ActionResultType.SUCCESS;
                }
            }
            return ActionResultType.FAIL;
        }

        protected abstract boolean doRakeAction(IAgriCrop crop, ItemStack stack, @Nullable PlayerEntity player);
    }
}
