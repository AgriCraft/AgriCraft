package com.infinityraider.agricraft.impl.v1;

import com.agricraft.agricore.lang.AgriTranslationAdapter;
import com.infinityraider.agricraft.AgriCraft;

public class ModTranslator implements AgriTranslationAdapter {

    @Override
    public String translateKey(String key) {
        return AgriCraft.instance.proxy().translateToLocal(key);
    }

    @Override
    public String getLocale() {
        return AgriCraft.instance.proxy().getLocale();
    }

}
