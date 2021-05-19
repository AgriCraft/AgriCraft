package com.infinityraider.agricraft.impl.v1.irrigation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public class IrrigationLayer {
    private final Set<SectionManager> sections;

    protected IrrigationLayer() {
        this.sections = Sets.newIdentityHashSet();
    }


    public static class SectionManager implements IFluidHandler {
        private final IrrigationLayer layer;
        private final IrrigationLayerSection section;
        private final Map<IrrigationLayerSection.Pos, Link> links;

        private final Set<SectionManager> pool;

        private SectionManager below;
        private SectionManager above;

        protected SectionManager(IrrigationLayer layer, IrrigationLayerSection section) {
            this.layer = layer;
            this.section = section;
            this.links = Maps.newHashMap();
            this.pool = Sets.newIdentityHashSet();
            
        }

        public IrrigationLayer getLayer() {
            return this.layer;
        }

        public IrrigationLayerSection getSection() {
            return this.section;
        }

        public void expand(BlockPos from, BlockPos to, int capacity, int content) {

        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            //TODO
            return null;
        }

        @Override
        public int getTankCapacity(int tank) {
            //TODO
            return 0;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            //TODO
            return false;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            //TODO
            return 0;
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            //TODO
            return null;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            //TODO
            return null;
        }
    }

    public static class Link {
        private final SectionManager first;
        private final IrrigationLayerSection.Pos firstPos;

        private final SectionManager second;
        private final IrrigationLayerSection.Pos secondPos;

        protected Link(SectionManager first, IrrigationLayerSection.Pos firstPos,
                       SectionManager second, IrrigationLayerSection.Pos secondPos) {
            this.first = first;
            this.firstPos = firstPos;
            this.second = second;
            this.secondPos = secondPos;
        }


    }
}
