package com.github.zlamb1.io.gif;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GIFLoader {
    protected ByteBuffer byteBuffer;
    protected IGIFFactory gifFactory;

    protected static final byte[] GIF87_HEADER = new byte[] { 'G', 'I', 'F', '8', '7', 'a' };
    protected static final byte[] GIF89_HEADER = new byte[] { 'G', 'I', 'F', '8', '9', 'a' };

    protected static final String GENERIC_PARSE_ERR = "Failed to parse GIF.";

    protected static final Integer[] CLEAR_CODE = new Integer[] {};
    protected static final Integer[] EOI_CODE = new Integer[] {};

    protected java.util.List<IGIFExtension> perImageDescriptorExtensions = new ArrayList<>();

    public GIFLoader(String resourcePath) throws IOException {
        URL url = getClass().getClassLoader().getResource(resourcePath);

        gifFactory = new GIFFactory();

        loadBytes(url);
        parseHeader();
        parseBody();
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

    protected void parseHeader() {
        gifFactory.setVersion(switch (expectByteSequence(GIF87_HEADER, GIF89_HEADER)) {
            case 0 -> GIF.GIFVersion.GIF87a;
            case 1 -> GIF.GIFVersion.GIF89a;
            default -> throw new RuntimeException("Invalid GIF Version");
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

    protected void parseBody() {
        byte nextByte;

        while ((nextByte = readByte("Unexpected EOF")) != ';') {
            switch (nextByte) {
                case ',' -> parseImageDescriptor();
                case '!' -> parseExtensionBlock();
                default -> throw new RuntimeException("Unexpected GIF Segment Sentinel: " + (char) nextByte);
            }
        }
    }

    protected void parseImageDescriptor() {
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
            throw new RuntimeException("Missing GIF Color Table");
        }

        int lzwMinCodeSize = unsignedByte(readByte());
        int codeSize = lzwMinCodeSize + 1;

        Map<Integer, Integer[]> codeTable = new HashMap<>();
        initializeCodeTable(codeTable, activeColorTable);

        ArrayList<Integer> indexStream = new ArrayList<>();

        byteBuffer.mark();
        discardImageBlocks();
        int endPosition = byteBuffer.position();
        byteBuffer.reset();

        boolean increaseCodeSize = false;
        int imageArea = width * height;
        int blockSize = 0;
        int lastCode = -1;
        int bitIndex = 0;
        int iterations = 0;

        outerLoop:
        while (indexStream.size() < imageArea) {
            if (blockSize <= 0) {
                int tmpBlockSize = blockSize;
                if ((blockSize = unsignedByte(readByte())) == 0x0) {
                    break;
                }
                for (int i = 0; i < -tmpBlockSize; i++) {
                    readByte();
                }
                blockSize += tmpBlockSize;
            }

            if (increaseCodeSize) {
                increaseCodeSize = false;
                codeSize++;
            }

            byteBuffer.mark();

            int curByte = (unsignedByte(readByte()) >> bitIndex) & ((1 << codeSize) - 1);
            int code = curByte;

            if (bitIndex + codeSize > 8) {
                int curByteBitCount = 8 - bitIndex;
                int nextByte;
                if (blockSize == 1) {
                    // consume block size
                    int nextBlockSize = unsignedByte(readByte());
                    if (nextBlockSize == 0x0) {
                        break;
                        //throw new RuntimeException("Failed to Parse GIF: Unexpected End of Image Descriptor");
                    }
                }

                nextByte = unsignedByte(readByte()) & ((1 << (codeSize - curByteBitCount)) - 1);
                code = curByte | (nextByte << curByteBitCount);
            }

            System.out.println(code);

            Integer[] codeValues = codeTable.get(code);
            switch (iterations) {
                case 0:
                    if (codeTable.get(code) != CLEAR_CODE) {
                        throw new RuntimeException("Invalid GIF Image Block: Expected Clear Code");
                    }
                    break;
                case 1:
                    indexStream.addAll(Arrays.asList(codeTable.get(code)));
                    lastCode = code;
                    break;
                default:
                    if (codeValues == CLEAR_CODE) {
                        System.out.println("Encountered CC");
                        initializeCodeTable(codeTable, activeColorTable);
                        codeSize = lzwMinCodeSize + 1;
                    } else if (codeValues == EOI_CODE) {
                        break outerLoop;
                    } else if (codeValues != null) {
                        indexStream.addAll(Arrays.asList(codeValues));
                        int k = codeValues[0];

                        Integer[] newCode = new Integer[codeTable.get(lastCode).length + 1];
                        for (int i = 0; i < codeTable.get(lastCode).length; i++) {
                            newCode[i] = codeTable.get(lastCode)[i];
                        }

                        newCode[newCode.length - 1] = k;

                        int newIndex = codeTable.size();
                        if (newIndex == Math.pow(2, codeSize) - 1) {
                            increaseCodeSize = true;
                        }

                        codeTable.put(newIndex, newCode);
                    } else {
                        int k = codeTable.get(lastCode)[0];
                        indexStream.addAll(Arrays.asList(codeTable.get(lastCode)));
                        indexStream.add(k);

                        Integer[] newCode = new Integer[codeTable.get(lastCode).length + 1];
                        for (int i = 0; i < codeTable.get(lastCode).length; i++) {
                            newCode[i] = codeTable.get(lastCode)[i];
                        }

                        newCode[newCode.length - 1] = k;

                        int newIndex = codeTable.size();
                        if (newIndex == Math.pow(2, codeSize) - 1) {
                            increaseCodeSize = true;
                        }

                        codeTable.put(newIndex, newCode);
                    }

                    lastCode = code;

                    break;
            }

            byteBuffer.reset();

            iterations++;
            bitIndex += codeSize;
            int tmpBlockSize = blockSize;
            blockSize -= bitIndex / 8;

            if (bitIndex / 8 >= 1) {
                if (blockSize <= 0) {
                    byteBuffer.position(byteBuffer.position() + tmpBlockSize);
                    blockSize = unsignedByte(readByte()) + blockSize;
                    byteBuffer.position(byteBuffer.position() + blockSize);
                } else {
                    byteBuffer.position(byteBuffer.position() + bitIndex / 8);
                }

                bitIndex %= 8;
            }
        }

        byteBuffer.position(endPosition);

        System.out.println(imageArea + " vs. " + indexStream.size());

        /*BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < indexStream.size(); i++) {
            int colorIdx = indexStream.get(i);
            int x = i % width;
            int y = i / width;
            image.setRGB(x, y, activeColorTable.getTable()[colorIdx].getRGB());
        }

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.add(new JLabel(new ImageIcon(image)));
        frame.setVisible(true);*/

        GIFImageDescriptor imageDescriptor = new GIFImageDescriptor(left, top, width, height, colorTable, isInterlaced);

        perImageDescriptorExtensions.clear();
    }

    protected int discardImageBlocks() {
        int accumulatedSize = 0;
        int blockSize;
        while ((blockSize = unsignedByte(readByte())) != 0x0) {
            accumulatedSize += blockSize;
            byteBuffer.position(byteBuffer.position() + blockSize);
        }
        return accumulatedSize;
    }

    protected void initializeCodeTable(Map<Integer, Integer[]> codeTable, GIFColorTable sourceColorTable) {
        codeTable.clear();

        for (int i = 0; i < sourceColorTable.getTable().length; i++) {
            codeTable.put(i, new Integer[] { i });
        }

        while (codeTable.size() <= 3) {
            codeTable.put(codeTable.size(), new Integer[] {});
        }

        codeTable.put(codeTable.size(), CLEAR_CODE);
        codeTable.put(codeTable.size(), EOI_CODE);
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

    protected byte[] getOrExcept(int numBytes) {
        return getOrExcept(numBytes, GENERIC_PARSE_ERR);
    }

    protected byte[] getOrExcept(int numBytes, String msg) {
        if (byteBuffer.remaining() < numBytes) {
            // FIXME: GIF exception?
            throw new RuntimeException(msg);
        }

        byte[] buffer = new byte[numBytes];
        byteBuffer.get(buffer);
        return buffer;
    }

    protected byte readByte() {
        return readByte(GENERIC_PARSE_ERR);
    }

    protected byte readByte(String msg) {
        if (!byteBuffer.hasRemaining()) {
            throw new RuntimeException(msg);
        }

        return byteBuffer.get();
    }

    protected short readShort() {
        byte[] data = getOrExcept(2);
        short value = data[1];
        value <<= 8;
        value |= (short) (data[0] & 0xFF);
        return value;
    }

    protected int readInt() {
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
