package com.github.zlamb1.io;

import java.util.Collection;

public interface Serializer<T> {
    void serialize(Collection<T> collection);
    Collection<T> deserialize() throws DeserializeException;
}
