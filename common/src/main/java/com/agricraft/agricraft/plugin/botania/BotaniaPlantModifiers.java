package com.agricraft.agricraft.plugin.botania;

public class BotaniaPlantModifiers {

//    public static final String ID = "agricraft:botania_mana";
//
//    public static void register() {
//        AgriGrowthConditionRegistry.BaseGrowthCondition<Integer> manaGrowthCondition = new AgriGrowthConditionRegistry.BaseGrowthCondition<>("mana", (plant, strength, cost) -> {
//            // Fetch custom tile data
//            CompoundTag tag = plant.explode();
//            // Fetch mana stored on the crop
//            int mana = tag.contains(ID) ? tag.getInt(ID) : 0;
//            // If there is not enough, try to fetch more
//            if(mana < cost) {
//                mana = mana + this.tryCollectMana(plant, 5*cost - mana);
//                tag.putInt(ID, mana);
//            }
//            // If there is enough mana, the crop may grow
//            // Note that we do not consume mana here, as this is just a fertility check, mana is consumed after the growth tick
//            return mana >= cost ? AgriGrowthResponse.FERTILE : AgriGrowthResponse.INFERTILE;
//        }, (level, blockPos) -> {
//            return null;
//        });
//        AgriPlantModifierFactoryRegistry.register(ID, info -> {
//            try {
//                return Optional.of(new ManaConsumerPlantModifier(Integer.parseInt(info.value())));
//            } catch (NumberFormatException ignored) {
//            }
//            return Optional.empty();
//        });
//        AgriGrowthConditionRegistry.getInstance().add(manaGrowthCondition);
//    }
//
//    public static class ManaConsumerPlantModifier implements IAgriPlantModifier {
//
//        private final int cost;
//
//        public ManaConsumerPlantModifier(int cost) {
//            this.cost = cost;
//        }
//
//        @Override
//        public void onGrowth(AgriCrop crop) {
//            // Consume mana
//            CompoundTag tag = crop.asBlockEntity().getUpdateTag();
//            int mana = tag.contains(ID) ? tag.getInt(ID) : 0;
//            tag.putInt(ID, Math.max(0, mana - this.cost));
//            // spawn a particle
//            BotaniaAPI.instance().sparkleFX(
//                    crop.getLevel(),
//                    crop.getBlockPos().getX() + 0.5 + 0.5*Math.random(),
//                    crop.getBlockPos().getY() + 0.5 + 0.5*Math.random(),
//                    crop.getBlockPos().getZ() + 0.5 + 0.5*Math.random(),
//                    67.0F/255.0F,
//                    180.0F/255.0F,
//                    1.0F,
//                    (float) Math.random(),
//                    5
//            );
//        }
//    }
}
