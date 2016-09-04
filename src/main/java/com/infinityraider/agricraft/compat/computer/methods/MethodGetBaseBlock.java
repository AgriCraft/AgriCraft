package com.infinityraider.agricraft.compat.computer.methods;

import net.minecraft.item.ItemStack;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.util.FuzzyStack;
import java.util.Optional;

public class MethodGetBaseBlock extends MethodBaseGrowthReq {
    public MethodGetBaseBlock() {
        super("getBaseBlock");
    }

    @Override
    protected Object[] onMethodCalled(IAgriPlant plant) {
        if(plant==null) {
            return null;
        }
        Optional<FuzzyStack> block = plant.getGrowthRequirement().getRequiredBlock();
        String msg = block.map(b -> b.toStack().getDisplayName()).orElse("null");
        return new Object[] {msg};
    }
}
