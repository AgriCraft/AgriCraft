/*
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.lang.AgriTranslationAdapter;
import com.infinityraider.agricraft.AgriCraft;

/**
 *
 *
 */
public class ModTranslator implements AgriTranslationAdapter {

    @Override
    public String translateKey(String key) {
        return AgriCraft.proxy.translateToLocal(key);
    }

    @Override
    public String getLocale() {
        return AgriCraft.proxy.getLocale();
    }

}
