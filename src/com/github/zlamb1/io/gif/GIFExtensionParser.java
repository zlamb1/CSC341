package com.github.zlamb1.io.gif;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GIFExtensionParser {
    public interface IExtensionParser {
        IGIFExtension parse(int extensionLabel, ByteBuffer extensionBytes, List<GIFExtensionBlock> blocks);
    }

    protected Map<Integer, IExtensionParser> extensionParsers;

    public GIFExtensionParser() {
        extensionParsers = new HashMap<>();

        extensionParsers.put(GIFGraphicsControlExtension.EXT_LABEL, GIFExtensionParser::parseGraphicsControlExtension);
    }

    public IGIFExtension parseExtension(ByteBuffer buffer) throws BufferUnderflowException {
        int extensionLabel = ((int) buffer.get()) & 0xFF;
        int byteSize = ((int) buffer.get()) & 0xFF;

        ByteBuffer extensionBytes = buffer.slice(buffer.position(), byteSize).order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(buffer.position() + byteSize);

        List<GIFExtensionBlock> blocks = new ArrayList<>();

        int blockByteSize;
        while ((blockByteSize = ((int)buffer.get()) & 0xFF) != 0x0) {
            int id = ((int)buffer.get()) & 0xFF;
            ByteBuffer blockBytes = buffer.slice(buffer.position(), blockByteSize - 1).order(ByteOrder.LITTLE_ENDIAN);
            buffer.position(buffer.position() + blockByteSize - 1);
            blocks.add(new GIFExtensionBlock(blockByteSize, id, blockBytes));
        }

        if (extensionParsers.containsKey(extensionLabel)) {
            return extensionParsers.get(extensionLabel).parse(extensionLabel, extensionBytes, blocks);
        }

        return new GIFExtension(extensionLabel, byteSize, extensionBytes, blocks);
    }

    protected static IGIFExtension parseGraphicsControlExtension(int extensionLabel, ByteBuffer extensionBytes, List<GIFExtensionBlock> blocks) throws BufferUnderflowException {
        minimumByteSize(extensionBytes.remaining(), 4,
            "Failed to parse graphics control extension: body must be at least four bytes.");

        byte packedField = extensionBytes.get();
        int disposalMethod = (packedField & 0b0001_1100) >> 2;
        boolean hasUserInput = (packedField & 0b0000_0010) == 0b0000_0010;
        boolean hasTransparency = (packedField & 0b0000_0001) == 0b0000_0001;

        int delayTime = extensionBytes.getShort() & 0xFFFF;
        int transparentColorIdx = unsignedByte(extensionBytes.get());

        return new GIFGraphicsControlExtension(extensionBytes.remaining(), extensionBytes,
                hasUserInput, hasTransparency, disposalMethod, delayTime, transparentColorIdx, blocks);
    }

    protected static void minimumByteSize(int realByteSize, int minimumByteSize, String msg) {
        if (realByteSize < minimumByteSize) {
            throw new RuntimeException(msg);
        }
    }

    protected static int unsignedByte(byte b) {
        return (int) b & 0xFF;
    }
}
