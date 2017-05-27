/*
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.lang.AgriTranslationAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.translation.I18n;

/**
 *
 *
 */
public class ModTranslator implements AgriTranslationAdapter {

    @Override
    public String translateKey(String key) {
        return I18n.translateToLocal(key);
    }

    @Override
    public String getLocale() {
        return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
    }

}
