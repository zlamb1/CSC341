package com.github.zlamb1.io.gif;

import java.nio.ByteBuffer;

public class GIFExtensionBlock {
    protected int byteSize;
    protected int id;
    protected ByteBuffer blockBytes;

    public GIFExtensionBlock(int byteSize, int id, ByteBuffer blockBytes) {
        this.byteSize = byteSize;
        this.id = id;
        this.blockBytes = blockBytes;
    }

    public int getByteSize() {
        return byteSize;
    }

    public int getID() {
        return id;
    }

    public ByteBuffer getBlockBytes() {
        return blockBytes;
    }
}
