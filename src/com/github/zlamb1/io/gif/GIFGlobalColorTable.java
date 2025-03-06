package com.github.zlamb1.io.gif;

import java.awt.*;

public class GIFGlobalColorTable extends GIFColorTable {
    protected byte backgroundColorIdx;
    protected byte pixelAspectRatio;

    public GIFGlobalColorTable(byte bitDepth, boolean isSorted, byte globalColorTableSize, byte backgroundColorIdx, byte pixelAspectRatio, Color[] table) {
        super(bitDepth, globalColorTableSize, isSorted, table);
        this.backgroundColorIdx = backgroundColorIdx;
        this.pixelAspectRatio = pixelAspectRatio;
    }

    public byte getBackgroundColorIdx() {
        return backgroundColorIdx;
    }

    public byte getPixelAspectRatio() {
        return pixelAspectRatio;
    }

    public String toString() {
        return "GlobalColorTable={\n" +
                "\tbitDepth=" + bitDepth + "\n" +
                "\tisSorted=" + isSorted + "\n" +
                "\tglobalColorTableSize=" + tableSize + "\n" +
                "\tbackgroundColorIdx=" + backgroundColorIdx + "\n" +
                "\tpixelAspectRatio=" + pixelAspectRatio + "\n" +
                "}";
    }
}
