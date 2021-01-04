package com.infinityraider.agricraft.impl.v1;

import com.agricraft.agricore.util.AgriConverter;
import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.util.TagUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;

import java.util.List;
import java.util.Optional;

public class ModConverter implements AgriConverter {
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> toStack(Class<T> token, String element, int amount, String tags, boolean useOreDict, List<String> ignoreTags) {
        return Optional.ofNullable(token)
                .filter(type -> TypeHelper.isType(ItemStack.class, type))
                .flatMap(type -> TagUtil.makeItemStack(ItemTags.getCollection(), element, amount, tags))
                .map(stack -> (T) stack);
    }
}
