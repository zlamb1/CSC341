package com.github.zlamb1.io;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

public abstract class FileSerializer<T> extends AbstractFileSerializer implements Serializer<T> {
    public FileSerializer(String outputFilePath) {
        super(outputFilePath);
    }

    public FileSerializer(File outputFile) {
        super(outputFile);
    }

    public void serialize(Collection<T> collection) {
        ensureOutputFile();
        try (PrintWriter writer = new PrintWriter(outputFile)) {
            serialize(writer, collection);
        } catch (IOException e) {
            throw new AssertionError("Failed to access output file: " + outputFile.getName(), e);
        }
    }

    public Collection<T> deserialize() throws DeserializeException {
        if (outputFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
                return deserialize(reader);
            } catch (IOException e) {
                throw new DeserializeException("Failed to read output file: " + outputFile.getName(), e);
            }
        }

        return new ArrayList<>();
    }

    protected abstract void serialize(PrintWriter writer, Collection<T> coll);
    protected abstract Collection<T> deserialize(BufferedReader reader) throws IOException;
}
