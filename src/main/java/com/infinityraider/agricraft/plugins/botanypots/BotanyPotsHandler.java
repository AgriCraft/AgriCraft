package com.infinityraider.agricraft.plugins.botanypots;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.CropCapability;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthResponse;
import com.infinityraider.agricraft.handler.VanillaPlantingHandler;
import com.infinityraider.agricraft.util.CropHelper;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.api.events.BotanyPotHarvestedEvent;
import net.darkhax.botanypots.api.events.LookupEvent;
import net.darkhax.botanypots.api.events.PotGrowCropEvent;
import net.darkhax.botanypots.crop.CropInfo;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BotanyPotsHandler {
    private static final BotanyPotsHandler INSTANCE = new BotanyPotsHandler();

    public static BotanyPotsHandler getInstance() {
        return INSTANCE;
    }

    private BotanyPotsHandler() {}

    // Stats and seed conversion
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onLookUp(LookupEvent.Crop event) {
        if(event.getCurrentLookup() instanceof AgriCropInfo) {
            event.setLookup(((AgriCropInfo) event.getCurrentLookup()).withStats(event.getItemStack()));
        } else {
            if(!AgriCraft.instance.getConfig().overrideVanillaFarming()) {
                return;
            }
            if(VanillaPlantingHandler.getInstance().isException(event.getItemStack())) {
                return;
            }
            AgriApi.getGenomeAdapterizer().valueOf(event.getItemStack()).ifPresent(genome -> {
                ItemStack seedStack = genome.toSeedStack();
                CropInfo info = BotanyPotHelper.getCropForItem(seedStack);
                if(info instanceof AgriCropInfo) {
                    event.setLookup(((AgriCropInfo) info).withStats(seedStack));
                    AgriCraft.instance.queueTask(() ->
                            event.getBotanyPot().setCrop(info, seedStack)
                    );
                }
            });
        }
    }

    // Fertility checks and weeds
    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCropGrowth(PotGrowCropEvent.Pre event) {
        if(event.getBotanyPot().getCrop() instanceof AgriCropInfo) {
            event.getBotanyPot().getCapability(CropCapability.getCapability()).ifPresent(crop -> {
                boolean cancel;
                if(CropHelper.rollForWeedAction(crop)) {
                    // Weed tick, run weed logic
                    cancel = true;
                    if(!crop.hasWeeds()) {
                        CropHelper.spawnWeeds(crop);
                    } else {
                        if(crop instanceof BotanyPotAgriCropInstance.Impl) {
                            BotanyPotAgriCropInstance.Impl cropImpl = (BotanyPotAgriCropInstance.Impl) crop;
                            if(cropImpl.incrementWeedCounter(event.getCurrentAmount())) {
                                if(AgriCraft.instance.getConfig().allowLethalWeeds()) {
                                    event.setAmount(-1);
                                    if (event.getBotanyPot().getCurrentGrowthTicks() <= 0) {
                                        event.getBotanyPot().setCrop(null, ItemStack.EMPTY);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Plant tick, run fertility checks
                    cancel = false;
                    IAgriGrowthResponse response = crop.getFertilityResponse();
                    if (response.killInstantly()) {
                        event.getBotanyPot().setCrop(null, ItemStack.EMPTY);
                        response.onPlantKilled(crop);
                        cancel = true;
                    } else if (response.isLethal()) {
                        event.setAmount(-1);
                        if (event.getBotanyPot().getCurrentGrowthTicks() <= 0) {
                            event.getBotanyPot().setCrop(null, ItemStack.EMPTY);
                            cancel = true;
                        }
                    }
                }
                if(cancel) {
                    event.setAmount(0);
                    event.setCanceled(true);
                    event.setResult(Event.Result.DENY);
                }
            });
        }
    }

    // Harvesting
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onHarvestPre(BotanyPotHarvestedEvent.Pre event) {
        if(event.getBotanyPot().getCrop() instanceof AgriCropInfo) {
            event.getBotanyPot().getCapability(CropCapability.getCapability()).ifPresent(crop -> {
                if(!crop.canBeHarvested(event.getHarvestingPlayer())) {
                    event.setCanceled(true);
                    event.setResult(Event.Result.DENY);
                }
            });
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onHarvestLoot(BotanyPotHarvestedEvent.LootGenerated event) {
        if(event.getBotanyPot().getCrop() instanceof AgriCropInfo) {
            event.getBotanyPot().getCapability(CropCapability.getCapability()).ifPresent(crop -> {
                event.getDrops().clear();
                crop.harvest(event.getDrops()::add, event.getHarvestingPlayer());
            });
        }
    }
}
