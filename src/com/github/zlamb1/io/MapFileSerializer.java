package com.github.zlamb1.io;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class MapFileSerializer<K, V> extends AbstractFileSerializer implements MapSerializer<K, V> {
    public MapFileSerializer(String outputFilePath) {
        super(outputFilePath);
    }

    public MapFileSerializer(File outputFile) {
        super(outputFile);
    }

    public void serialize(Map<K, V> map) {
        ensureOutputFile();
        try (PrintWriter writer = new PrintWriter(outputFile)) {
            serialize(writer, map);
        } catch (IOException e) {
            throw new AssertionError("Failed to access output file: " + outputFile.getName(), e);
        }
    }

    public Map<K, V> deserialize() throws DeserializeException {
        if (outputFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
                return deserialize(reader);
            } catch (IOException e) {
                throw new DeserializeException("Failed to access output file: " + outputFile.getName(), e);
            }
        }

        return new HashMap<>();
    }

    protected abstract void serialize(PrintWriter writer, Map<K, V> coll);
    protected abstract Map<K, V> deserialize(BufferedReader reader) throws IOException;
}
