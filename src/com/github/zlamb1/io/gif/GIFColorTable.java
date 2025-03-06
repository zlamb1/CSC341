package com.github.zlamb1.io.gif;

import java.awt.*;

public class GIFColorTable {
    protected byte bitDepth;
    protected byte tableSize;

    protected boolean isSorted;

    protected Color[] table;

    public GIFColorTable(byte bitDepth, byte tableSize, boolean isSorted, Color[] table) {
        this.bitDepth = bitDepth;
        this.tableSize = tableSize;
        this.isSorted = isSorted;
        this.table = table;
    }

    public byte getBitDepth() {
        return bitDepth;
    }

    public byte getTableSize() {
        return tableSize;
    }

    public int getEntryCount() {
        return (int) Math.pow(2, tableSize);
    }

    public int getByteSize() {
        return getEntryCount() * 3;
    }

    public boolean isSorted() {
        return isSorted;
    }

    public Color[] getTable() {
        return table;
    }
}
