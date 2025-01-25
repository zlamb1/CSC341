package com.github.zlamb1.io;

import java.io.File;
import java.io.IOException;

public class AbstractFileSerializer {
    protected final File outputFile;

    public AbstractFileSerializer(String outputFilePath) {
        outputFile = new File(outputFilePath);
    }

    public AbstractFileSerializer(File outputFile) {
        this.outputFile = outputFile;
    }

    protected void ensureOutputFile() {
        try {
            // ensure file exists
            outputFile.createNewFile();
        } catch (IOException e) {
            throw new AssertionError("Failed to create output file: " + outputFile.getAbsolutePath(), e);
        }
    }
}
