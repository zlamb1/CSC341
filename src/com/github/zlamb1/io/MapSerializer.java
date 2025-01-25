package com.github.zlamb1.io;

import java.util.Map;

public interface MapSerializer<K, V> {
    void serialize(Map<K, V> map);
    Map<K, V> deserialize() throws DeserializeException;
}
