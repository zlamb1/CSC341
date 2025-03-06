package com.github.zlamb1.io.gif;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class GIFExtension implements IGIFExtension {
    protected int extensionLabel;
    protected int byteSize;
    protected ByteBuffer extensionBytes;
    protected List<GIFExtensionBlock> blocks;

    public GIFExtension(int extensionLabel, int byteSize, ByteBuffer extensionBytes) {
        this(extensionLabel, byteSize, extensionBytes, new ArrayList<>());
    }

    public GIFExtension(int extensionLabel, int byteSize, ByteBuffer extensionBytes, List<GIFExtensionBlock> blocks) {
        this.extensionLabel = extensionLabel;
        this.byteSize = byteSize;
        this.extensionBytes = extensionBytes;
        this.blocks = blocks;
    }

    @Override
    public int getExtensionLabel() {
        return extensionLabel;
    }

    @Override
    public int getByteSize() {
        return byteSize;
    }

    @Override
    public ByteBuffer getExtensionBytes() {
        return extensionBytes;
    }

    @Override
    public List<GIFExtensionBlock> getBlocks() {
        return blocks;
    }
}
