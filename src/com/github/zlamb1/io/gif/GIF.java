package com.github.zlamb1.io.gif;

import java.awt.*;

public class GIF {
    public enum GIFVersion {
        GIF87a,
        GIF89a
    }

    protected GIFVersion version;
    protected int logicalWidth;
    protected int logicalHeight;

    protected GIFGlobalColorTable globalColorTable;

    protected java.util.List<IGIFExtension> extensions;
    protected java.util.List<GIFImageDescriptor> imageDescriptors;

    public GIF(GIFVersion version, int logicalWidth, int logicalHeight,
               GIFGlobalColorTable globalColorTable, java.util.List<IGIFExtension> extensions,
               java.util.List<GIFImageDescriptor> imageDescriptors) {
        this.version = version;
        this.logicalWidth = logicalWidth;
        this.logicalHeight = logicalHeight;
        this.globalColorTable = globalColorTable;
        this.extensions = extensions;
        this.imageDescriptors = imageDescriptors;
    }

    public GIFVersion getVersion() {
        return version;
    }

    public int getLogicalWidth() {
        return logicalWidth;
    }

    public int getLogicalHeight() {
        return logicalHeight;
    }

    public GIFGlobalColorTable getGlobalColorTable() {
        return globalColorTable;
    }

    public java.util.List<IGIFExtension> getExtensions() {
        return extensions;
    }

    public java.util.List<GIFImageDescriptor> getImageDescriptors() {
        return imageDescriptors;
    }
}
