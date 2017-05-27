/*
 */
package com.infinityraider.agricraft.network.json;

import com.agricraft.agricore.json.AgriSerializable;
import com.google.gson.Gson;
import com.infinityraider.infinitylib.network.serialization.ByteBufUtil;
import com.infinityraider.infinitylib.network.serialization.IMessageReader;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import com.infinityraider.infinitylib.network.serialization.IMessageWriter;

/*
 *
 * @author Ryan
 */
public class JsonSerializer<T extends AgriSerializable> implements IMessageSerializer<T> {

    private final Gson GSON = new Gson();

    @Override
    public boolean accepts(Class<T> clazz) {
        return AgriSerializable.class.isAssignableFrom(clazz);
    }

    @Override
    public IMessageWriter<T> getWriter(Class<T> clazz) {
        return (buf, data) -> ByteBufUtil.writeString(buf, GSON.toJson(data, clazz));
    }

    @Override
    public IMessageReader<T> getReader(Class<T> clazz) {
        return (buf) -> GSON.fromJson(ByteBufUtil.readString(buf), clazz);
    }

}
