package com.github.zlamb1.io.gif;

import java.util.List;

public interface IGIFFactory {
    IGIFFactory setVersion(GIF.GIFVersion version);
    IGIFFactory setLogicalWidth(int logicalWidth);
    IGIFFactory setLogicalHeight(int logicalHeight);
    IGIFFactory setGlobalColorTable(GIFGlobalColorTable globalColorTable);

    IGIFFactory setExtensions(List<IGIFExtension> extensions);
    IGIFFactory addExtension(IGIFExtension extension);

    IGIFFactory setImageDescriptors(List<GIFImageDescriptor> imageDescriptors);
    IGIFFactory addImageDescriptor(GIFImageDescriptor imageDescriptor);

    GIF.GIFVersion getVersion();
    int getLogicalWidth();
    int getLogicalHeight();
    GIFGlobalColorTable getGlobalColorTable();
    List<IGIFExtension> getExtensions();
    List<GIFImageDescriptor> getImageDescriptors();

    GIF buildGIF();
}
