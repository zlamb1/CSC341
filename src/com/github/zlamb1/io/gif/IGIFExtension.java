package com.github.zlamb1.io.gif;

import java.nio.ByteBuffer;
import java.util.List;

public interface IGIFExtension {
    int getExtensionLabel();
    int getByteSize();
    ByteBuffer getExtensionBytes();
    List<GIFExtensionBlock> getBlocks();
}
