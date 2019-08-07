/*
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.util.AgriConverter;
import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.api.v1.util.FuzzyStack;
import com.infinityraider.agricraft.utility.OreDictUtil;
import java.util.List;
import java.util.Optional;
/**
 *
 *
 */
public class ModConverter implements AgriConverter {

    @Override
    public <T> Optional<T> toStack(Class<T> token, String element, int meta, int amount, String tags, boolean ignoreMeta, boolean useOreDict, List<String> ignoreTags) {
        return Optional.ofNullable(token)
                .filter(type -> TypeHelper.isType(FuzzyStack.class, type))
                .flatMap(type -> OreDictUtil.makeItemStack(element, meta, amount, tags))
                .map(stack -> (T) new FuzzyStack(stack, ignoreMeta, useOreDict, ignoreTags));
    }
    
}
