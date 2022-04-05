package com.infinityraider.agricraft.content.core;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.util.debug.*;
import com.infinityraider.infinitylib.item.ItemDebuggerBase;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import java.util.List;

public class ItemDebugger extends ItemDebuggerBase {
    private static final List<DebugMode> MODES = ImmutableList.of(
            new DebugModeCheckSoil(),
            new DebugModeCoreInfo(),
            new DebugModeCheckIrrigationComponent(),
            new DebugModeFillIrrigationComponent(),
            new DebugModeDrainIrrigationComponent(),
            new DebugModeBonemeal(),
            new DebugModeDiffLight(),
            new DebugModeGreenHouse()
    );

    public ItemDebugger() {
        super(true);
    }

    @Override
    protected List<DebugMode> getDebugModes() {
        return MODES;
    }
}
