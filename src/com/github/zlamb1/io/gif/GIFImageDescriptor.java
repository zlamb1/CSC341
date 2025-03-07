package com.github.zlamb1.io.gif;

import java.util.List;

public class GIFImageDescriptor {
    protected int left;
    protected int top;
    protected int width;
    protected int height;

    protected GIFColorTable colorTable;

    protected boolean isInterlaced;

    protected List<Integer> indexStream;
    protected List<IGIFExtension> perImageDescriptorExtensions;

    public GIFImageDescriptor(int left, int top, int width, int height,
                              GIFColorTable colorTable, boolean isInterlaced,
                              List<Integer> indexStream, List<IGIFExtension> perImageDescriptorExtensions) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.colorTable = colorTable;
        this.isInterlaced = isInterlaced;
        this.indexStream = indexStream;
        this.perImageDescriptorExtensions = perImageDescriptorExtensions;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public GIFColorTable getColorTable() {
        return colorTable;
    }

    public boolean isInterlaced() {
        return isInterlaced;
    }

    public List<Integer> getIndexStream() {
        return indexStream;
    }

    public List<IGIFExtension> getPerImageDescriptorExtensions() {
        return perImageDescriptorExtensions;
    }
}
