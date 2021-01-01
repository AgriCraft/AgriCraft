package com.infinityraider.agricraft.core;

import com.agricraft.agricore.lang.AgriTranslationAdapter;
import com.infinityraider.agricraft.AgriCraft;
import net.minecraft.client.resources.I18n;

public class ModTranslator implements AgriTranslationAdapter {

    @Override
    public String translateKey(String key) {
        return I18n.format(key);
    }

    @Override
    public String getLocale() {
        return "";
    }

}
