package com.github.zlamb1.io.gif;

import java.util.ArrayList;
import java.util.List;

public class GIFFactory implements IGIFFactory {
    protected GIF.GIFVersion version = GIF.GIFVersion.GIF87a;
    protected int logicalWidth = 0;
    protected int logicalHeight = 0;

    protected GIFGlobalColorTable globalColorTable = null;

    protected List<IGIFExtension> extensions = new ArrayList<>();
    protected List<GIFImageDescriptor> imageDescriptors = new ArrayList<>();

    @Override
    public IGIFFactory setVersion(GIF.GIFVersion version) {
        this.version = version;
        return this;
    }

    @Override
    public IGIFFactory setLogicalWidth(int logicalWidth) {
        this.logicalWidth = logicalWidth;
        return this;
    }

    @Override
    public IGIFFactory setLogicalHeight(int logicalHeight) {
        this.logicalHeight = logicalHeight;
        return this;
    }

    @Override
    public IGIFFactory setGlobalColorTable(GIFGlobalColorTable globalColorTable) {
        this.globalColorTable = globalColorTable;
        return this;
    }

    @Override
    public IGIFFactory setExtensions(List<IGIFExtension> extensions) {
        this.extensions = extensions;
        return this;
    }

    @Override
    public IGIFFactory addExtension(IGIFExtension extension) {
        this.extensions.add(extension);
        return this;
    }

    @Override
    public IGIFFactory setImageDescriptors(List<GIFImageDescriptor> imageDescriptors) {
        this.imageDescriptors = imageDescriptors;
        return this;
    }

    @Override
    public IGIFFactory addImageDescriptor(GIFImageDescriptor imageDescriptor) {
        imageDescriptors.add(imageDescriptor);
        return this;
    }

    @Override
    public GIF.GIFVersion getVersion() {
        return version;
    }

    @Override
    public int getLogicalWidth() {
        return logicalWidth;
    }

    @Override
    public int getLogicalHeight() {
        return logicalHeight;
    }

    @Override
    public GIFGlobalColorTable getGlobalColorTable() {
        return globalColorTable;
    }

    @Override
    public List<IGIFExtension> getExtensions() {
        return extensions;
    }

    @Override
    public List<GIFImageDescriptor> getImageDescriptors() {
        return List.of();
    }

    @Override
    public GIF buildGIF() {
        return new GIF(version, logicalWidth, logicalHeight, globalColorTable, extensions, imageDescriptors);
    }
}
