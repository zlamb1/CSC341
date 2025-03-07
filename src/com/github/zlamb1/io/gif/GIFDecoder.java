package com.github.zlamb1.io.gif;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GIFDecoder {
    protected ByteBuffer byteBuffer;
    protected IGIFFactory gifFactory;

    protected static final byte[] GIF87_HEADER = new byte[] { 'G', 'I', 'F', '8', '7', 'a' };
    protected static final byte[] GIF89_HEADER = new byte[] { 'G', 'I', 'F', '8', '9', 'a' };

    protected java.util.List<IGIFExtension> perImageDescriptorExtensions = new ArrayList<>();

    public GIFDecoder(String resourcePath) throws IOException {
        URL url = getClass().getClassLoader().getResource(resourcePath);

        gifFactory = new GIFFactory();

        loadBytes(url);
        parseHeader();
        parseBody();
    }

    public GIF getGIF() {
        return gifFactory.buildGIF();
    }

    protected void loadBytes(URL url) throws IOException {
        try (InputStream inputStream = url.openStream();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        ) {
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            byte[] byteArray = byteArrayOutputStream.toByteArray();

            // GIF format is strictly little-endian
            byteBuffer = ByteBuffer.wrap(byteArray).asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN);
        }
    }

    protected void parseHeader() throws GIFDecodeException {
        gifFactory.setVersion(switch (expectByteSequence(GIF87_HEADER, GIF89_HEADER)) {
            case 0 -> GIF.GIFVersion.GIF87a;
            case 1 -> GIF.GIFVersion.GIF89a;
            default -> throw new GIFDecodeException(GIFDecodeException.DecodeCause.INVALID_VERSION);
        });

        gifFactory.setLogicalWidth(unsignedShort(readShort()));
        gifFactory.setLogicalHeight(unsignedShort(readShort()));

        byte packedByte = readByte();
        byte backgroundColorIdx = readByte();
        byte pixelAspectRatio = readByte();

        boolean hasGlobalColorTable = (packedByte & 0b1000_0000) == 0b1000_0000;

        if (hasGlobalColorTable) {
            byte bitDepth = (byte) (((packedByte & 0b0111_0000) >> 4) + 1);
            boolean isSorted = (packedByte & 0b0000_1000) == 0b0000_1000;
            byte globalColorTableSize = (byte) ((packedByte & 0b0000_0111) + 1);

            int numEntries = (int) Math.pow(2, globalColorTableSize);
            Color[] table = new Color[numEntries];
            byte[] byteArray = getOrExcept(numEntries * 3);

            for (int i = 0; i < numEntries; i++) {
                int byteIdx = i * 3;
                table[i] = new Color(unsignedByte(byteArray[byteIdx]), unsignedByte(byteArray[byteIdx + 1]), unsignedByte(byteArray[byteIdx + 2]));
            }

            GIFGlobalColorTable globalColorTable = new GIFGlobalColorTable(
                bitDepth,
                isSorted,
                globalColorTableSize,
                backgroundColorIdx,
                pixelAspectRatio,
                table
            );

            gifFactory.setGlobalColorTable(globalColorTable);
        }
    }

    protected void parseBody() throws GIFDecodeException {
        byte nextByte;

        while ((nextByte = readByte()) != ';') {
            switch (nextByte) {
                case ',' -> parseImageDescriptor();
                case '!' -> parseExtensionBlock();
                default -> throw new GIFDecodeException(GIFDecodeException.DecodeCause.INVALID_FORMAT, "Expected Sentinel; Got " + (char) nextByte);
            }
        }
    }

    protected void parseImageDescriptor() throws GIFDecodeException {
        int left = unsignedShort(readShort());
        int top = unsignedShort(readShort());
        int width = unsignedShort(readShort());
        int height = unsignedShort(readShort());

        byte packedByte = readByte();

        boolean hasColorTable = (packedByte & 0b1000_0000) == 0b1000_0000;
        boolean isSorted = (packedByte & 0b0010_0000) == 0b0010_0000;
        byte tableSize = (byte) ((packedByte & 0b0000_0111) + 1);

        GIFColorTable colorTable = null;

        if (hasColorTable) {
            int numEntries = (int) Math.pow(2, tableSize);
            byte[] byteArray = new byte[numEntries * 3];
            byteBuffer.get(byteArray);

            Color[] table = new Color[numEntries];

            for (int i = 0; i < numEntries; i++) {
                int byteIdx = i * 3;
                table[i] = new Color(unsignedByte(byteArray[byteIdx]), unsignedByte(byteArray[byteIdx + 1]), unsignedByte(byteArray[byteIdx + 2]));
            }

            colorTable = new GIFColorTable(tableSize, tableSize, isSorted, table);
        }

        boolean isInterlaced = (packedByte & 0b0100_0000) == 0b0100_0000;

        GIFColorTable activeColorTable = hasColorTable ? colorTable : gifFactory.getGlobalColorTable();
        if (activeColorTable == null) {
            throw new GIFDecodeException(GIFDecodeException.DecodeCause.MISSING_COLOR_TABLE);
        }

        int lzwMinCodeSize = unsignedByte(readByte());
        int codeSize = lzwMinCodeSize + 1;
        int imageArea = width * height;

        int clearCode = (int) Math.pow(2, lzwMinCodeSize);
        int eoiCode = clearCode + 1;

        // find end position
        byteBuffer.mark();
        discardImageBlocks();
        int endPosition = byteBuffer.position();
        byteBuffer.reset();

        MutableInteger shift = new MutableInteger();
        MutableInteger blockSize = new MutableInteger();

        Map<Integer, Integer[]> codeTable = new HashMap<>();
        initializeCodeTable(codeTable, lzwMinCodeSize);

        ArrayList<Integer> indexStream = new ArrayList<>();

        int startCode = getCode(codeSize, shift, blockSize);
        if (startCode != clearCode) {
            throw new GIFDecodeException(GIFDecodeException.DecodeCause.INVALID_FORMAT, "Expected Clear Code; Got " + startCode);
        }

        int firstCode = getCode(codeSize, shift, blockSize);
        if (firstCode == -1) {
            throw new GIFDecodeException(GIFDecodeException.DecodeCause.INVALID_FORMAT, "Expected First Code");
        }

        indexStream.add(firstCode);

        int lastCode = firstCode;
        while (indexStream.size() < imageArea) {
            int code = getCode(codeSize, shift, blockSize);
            boolean isTableFull = codeTable.size() >= 4095;

            if (code == clearCode) {
                codeSize = lzwMinCodeSize + 1;
                initializeCodeTable(codeTable, lzwMinCodeSize);
                firstCode = lastCode = getCode(codeSize, shift, blockSize);
                indexStream.add(firstCode);
                continue;
            }

            if (code == eoiCode) {
                break;
            }

            Integer[] lastValues = codeTable.get(lastCode);
            if (codeTable.containsKey(code)) {
                Integer[] values = codeTable.get(code);
                for (int i : values)
                    indexStream.add(i);
                if (!isTableFull && putCode(codeTable, codeSize, lastValues, values[0]))
                    codeSize++;
            } else {
                int k = lastValues[0];
                for (int i : lastValues)
                    indexStream.add(i);
                indexStream.add(k);
                if (!isTableFull && putCode(codeTable, codeSize, lastValues, k))
                    codeSize++;
            }

            lastCode = code;
        }

        byteBuffer.position(endPosition);

        GIFImageDescriptor imageDescriptor = new GIFImageDescriptor(left, top, width, height, activeColorTable,
                isInterlaced, indexStream, new ArrayList<>(perImageDescriptorExtensions));
        gifFactory.addImageDescriptor(imageDescriptor);

        perImageDescriptorExtensions.clear();
    }

    protected int getCode(int codeSize, MutableInteger shift, MutableInteger blockSize) throws GIFDecodeException {
        int bitsRemaining = codeSize;
        int lpad = shift.value;
        int code = 0;

        while (bitsRemaining > 0) {
            if (blockSize.value == 0) {
                blockSize.value = unsignedByte(readByte());
                if (blockSize.value == 0) {
                    return -1;
                }
            }

            byteBuffer.mark();

            int bitIndex = codeSize - bitsRemaining;
            int rpad = bitsRemaining < 8 ? Math.max((8 - bitsRemaining) - lpad, 0) : 0;
            int bitSize = 8 - lpad - rpad;
            int b = unsignedByte(readByte());
            int nextByte = (b >> lpad) & ((int) Math.pow(2, bitSize) - 1);

            code |= (nextByte << bitIndex);

            if (bitsRemaining + lpad >= 8) {
                blockSize.value--;
            } else {
                byteBuffer.reset();
            }

            bitsRemaining -= bitSize;
            lpad = 0;
        }

        shift.value = (shift.value + codeSize) % 8;

        return code;
    }

    protected boolean putCode(Map<Integer, Integer[]> codeTable, int codeSize, Integer[] values, int k) {
        Integer[] newValues = new Integer[values.length + 1];

        for (int i = 0; i < values.length; i++)
            newValues[i] = values[i];

        newValues[newValues.length - 1] = k;

        codeTable.put(codeTable.size(), newValues);

        return codeTable.size() == Math.pow(2, codeSize);
    }

    protected int discardImageBlocks() throws GIFDecodeException {
        int accumulatedSize = 0;
        int blockSize;
        while ((blockSize = unsignedByte(readByte())) != 0x0) {
            accumulatedSize += blockSize;
            byteBuffer.position(byteBuffer.position() + blockSize);
        }
        return accumulatedSize;
    }

    protected void initializeCodeTable(Map<Integer, Integer[]> codeTable, int minCodeSize) {
        codeTable.clear();

        for (int i = 0; i < Math.pow(2, minCodeSize); i++) {
            codeTable.put(i, new Integer[] { i });
        }

        while (codeTable.size() <= 3) {
            codeTable.put(codeTable.size(), new Integer[] {});
        }

        Integer[] placeholder = new Integer[0];

        // clear code
        codeTable.put(codeTable.size(), placeholder);
        // EOI code
        codeTable.put(codeTable.size(), placeholder);
    }

    protected void parseExtensionBlock() {
        IGIFExtension extension = new GIFExtensionParser().parseExtension(byteBuffer);

        gifFactory.addExtension(extension);
        perImageDescriptorExtensions.add(extension);
    }

    protected int unsignedByte(byte b) {
        return b >= 0 ? b : 256 + ((int) b);
    }

    protected int unsignedShort(short s) {
        return s >= 0 ? s : 65536 + ((int) s);
    }

    protected byte[] getOrExcept(int numBytes) throws GIFDecodeException {
        return getOrExcept(numBytes, null);
    }

    protected byte[] getOrExcept(int numBytes, String ctx) throws GIFDecodeException {
        if (byteBuffer.remaining() < numBytes) {
            throw new GIFDecodeException(GIFDecodeException.DecodeCause.INVALID_FORMAT, ctx);
        }

        byte[] buffer = new byte[numBytes];
        byteBuffer.get(buffer);
        return buffer;
    }

    protected byte readByte() throws GIFDecodeException {
        return readByte(null);
    }

    protected byte readByte(String ctx) throws GIFDecodeException {
        if (!byteBuffer.hasRemaining()) {
            throw new GIFDecodeException(GIFDecodeException.DecodeCause.INVALID_FORMAT, ctx);
        }

        return byteBuffer.get();
    }

    protected short readShort() throws GIFDecodeException {
        byte[] data = getOrExcept(2);
        short value = data[1];
        value <<= 8;
        value |= (short) (data[0] & 0xFF);
        return value;
    }

    protected int readInt() throws GIFDecodeException {
        byte[] data = getOrExcept(4);
        int value = data[3] << 24;
        value |= (data[2] & 0xFF) << 16;
        value |= (data[1] & 0xFF) << 8;
        value |= data[0] & 0xFF;
        return value;
    }

    protected int expectByteSequence(byte[]... sequences) {
        if (!byteBuffer.hasRemaining()) {
            return -1;
        }

        byteBuffer.mark();
        for (int sequenceIndex = 0; sequenceIndex < sequences.length; sequenceIndex++) {
            byte[] sequence = sequences[sequenceIndex];

            if (byteBuffer.remaining() < sequence.length) {
                continue;
            }

            byte[] actualSequence = new byte[sequence.length];
            byteBuffer.get(actualSequence);

            if (Arrays.equals(actualSequence, sequence)) {
                return sequenceIndex;
            }

            byteBuffer.reset();
        }

        return -1;
    }
}
