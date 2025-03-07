package com.github.zlamb1.io.gif;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GIFLoader {
    protected String resourcePath;
    protected GIFDecoder decoder;

    public GIFLoader(String resourcePath) throws IOException {
        this.resourcePath = resourcePath;
        decoder = new GIFDecoder(resourcePath);
    }

    public List<GIFFrame> getFrames() {
        GIF gif = decoder.getGIF();

        List<GIFFrame> frames = new ArrayList<>();
        List<GIFImageDescriptor> imageDescriptors = gif.getImageDescriptors();

        for (GIFImageDescriptor imageDescriptor : imageDescriptors) {
            GIFColorTable colorTable = imageDescriptor.getColorTable();
            List<Integer> indexStream = imageDescriptor.getIndexStream();

            int imageWidth = imageDescriptor.getWidth();
            int imageHeight = imageDescriptor.getHeight();
            BufferedImage image = new BufferedImage(gif.getLogicalWidth(), gif.getLogicalHeight(), BufferedImage.TYPE_INT_ARGB);

            if (indexStream == null || indexStream.size() < (imageWidth * imageHeight) || colorTable == null)
                continue;

            GIFGraphicsControlExtension graphicsControlExt = null;
            for (IGIFExtension ext : imageDescriptor.getPerImageDescriptorExtensions()) {
                if (ext instanceof GIFGraphicsControlExtension) {
                    graphicsControlExt = (GIFGraphicsControlExtension) ext;
                }
            }

            int transparentColorIdx = graphicsControlExt == null ? -1 : graphicsControlExt.getTransparentColorIdx();
            boolean requiresUserInput = graphicsControlExt != null && graphicsControlExt.hasUserInput();

            for (int i = 0; i < indexStream.size(); i++) {
                int idx = indexStream.get(i);
                if (idx < 0 || idx >= colorTable.getTable().length)
                    continue;

                Color color;
                if (idx == transparentColorIdx) {
                    color = new Color(0, 0, 0, 0);
                } else {
                    color = colorTable.getTable()[idx];
                }

                int x = (i % imageWidth) + imageDescriptor.getLeft();
                int y = (i / imageWidth) + imageDescriptor.getTop();

                // ignore out of bounds pixel
                if (x < 0 || y < 0 || x >= gif.getLogicalWidth() || y >= gif.getLogicalHeight())
                    continue;

                image.setRGB(x, y, color.getRGB());
            }

            frames.add(new GIFFrame(image, 0, transparentColorIdx != -1, requiresUserInput));
        }

        return frames;
    }
}
