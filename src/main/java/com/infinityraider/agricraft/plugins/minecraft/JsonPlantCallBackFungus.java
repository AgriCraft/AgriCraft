package com.infinityraider.agricraft.plugins.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import net.minecraft.block.Block;
import net.minecraft.block.FungusBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class JsonPlantCallBackFungus implements IJsonPlantCallback {
    public static final String ID = AgriCraft.instance.getModId() + ":" + "fungus";

    private static final String FUNGUS = "fungus";

    private static final IJsonPlantCallback.Factory FACTORY = new IJsonPlantCallback.Factory() {
        @Override
        public String getId() {
            return ID;
        }

        @Override
        public IJsonPlantCallback makeCallBack(JsonElement json) throws JsonParseException {
            if(json instanceof JsonObject) {
                JsonObject obj = json.getAsJsonObject();
                if(obj.has(FUNGUS)) {
                    Block fungus = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(obj.get(FUNGUS).getAsString()));
                    if(fungus instanceof FungusBlock) {
                        return new JsonPlantCallBackFungus((FungusBlock) fungus);
                    }
                    throw new JsonParseException("Invalid fungus id.");
                }
                throw new JsonParseException("Json object does not contain a \"" + FUNGUS + "\" field.");
            }
            throw new JsonParseException("Not a json object.");
        }
    };

    public static IJsonPlantCallback.Factory getFactory() {
        return FACTORY;
    }

    private final FungusBlock fungus;

    private JsonPlantCallBackFungus(FungusBlock fungus) {
        this.fungus = fungus;
    }

    @Override
    public Optional<ActionResultType> onRightClickPre(@Nonnull IAgriCrop crop, @Nonnull ItemStack stack, @Nullable Entity entity) {
        // Fetch world
        World world = crop.world();
        // Perform checks
        if(stack.isEmpty()) {
            return Optional.empty();
        }
        if(stack.getItem() != Items.BONE_MEAL) {
            return Optional.empty();
        }
        if(world == null) {
            return Optional.empty();
        }
        if(!crop.isMature()) {
            return Optional.empty();
        }
        if(!this.fungus.canGrow(world, crop.getPosition(), crop.getBlockState(), world.isRemote())) {
            return Optional.empty();
        }
        if(!this.fungus.canUseBonemeal(world, world.getRandom(), crop.getPosition(), crop.getBlockState())) {
            return Optional.empty();
        }
        // grow
        if(world instanceof ServerWorld) {
            fungus.grow((ServerWorld) world, world.getRandom(), crop.getPosition(), crop.getBlockState());
            world.playEvent(2005, crop.getPosition(), 0);
        }
        return Optional.of(ActionResultType.SUCCESS);
    }
}
