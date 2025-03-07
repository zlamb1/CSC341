package com.github.zlamb1.assignment6;

import com.github.zlamb1.io.gif.GIFFrame;
import com.github.zlamb1.io.gif.GIFLoader;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpriteFactory {
    private static final Map<String, List<GIFFrame>> cachedResources = new HashMap<>();

    public static Sprite createSprite(String path, Dimension size) {
        List<Image> frames = new ArrayList<>();
        List<GIFFrame> gifFrames;

        if (cachedResources.containsKey(path)) {
            gifFrames = cachedResources.get(path);
        } else {
            try {
                GIFLoader loader = new GIFLoader(path);
                gifFrames = loader.getFrames();
                cachedResources.put(path, gifFrames);
            } catch (IOException exc) {
                throw new SpriteLoadException("Failed to Load Sprite");
            }
        }

        for (GIFFrame frame : gifFrames) {
            frames.add(frame.getImage().getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH));
        }

        return new Sprite(frames, size);
    }
}
