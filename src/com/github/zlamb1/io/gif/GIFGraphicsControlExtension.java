package com.github.zlamb1.io.gif;

import java.nio.ByteBuffer;
import java.util.List;

public class GIFGraphicsControlExtension extends GIFExtension {
    public static int EXT_LABEL = 0xF9;

    public enum DisposalMethod {
        NONE,
        NO_DISPOSAL,
        RESTORE_BACKGROUND,
        RESTORE_TO_PREVIOUS,
    }

    /* should expect user input before continuing */
    protected boolean hasUserInput;
    protected boolean hasTransparency;

    protected DisposalMethod disposalMethod;
    protected int delayTime;
    protected int transparentColorIdx;

    public GIFGraphicsControlExtension(int byteSize, ByteBuffer extensionBytes, boolean hasUserInput, boolean hasTransparency, int disposalMethod, int delayTime, int transparentColorIdx, List<GIFExtensionBlock> subBlocks) {
        super(EXT_LABEL, byteSize, extensionBytes, subBlocks);
        this.hasUserInput = hasUserInput;
        this.hasTransparency = hasTransparency;
        this.disposalMethod = switch (disposalMethod) {
            case 1 -> DisposalMethod.NO_DISPOSAL;
            case 2 -> DisposalMethod.RESTORE_BACKGROUND;
            case 3 -> DisposalMethod.RESTORE_TO_PREVIOUS;
            default -> DisposalMethod.NONE;
        };
        this.delayTime = delayTime;
        this.transparentColorIdx = transparentColorIdx;
    }

    public boolean hasUserInput() {
        return hasUserInput;
    }

    public boolean hasTransparency() {
        return hasTransparency;
    }

    public DisposalMethod getDisposalMethod() {
        return disposalMethod;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public int getTransparentColorIdx() {
        return transparentColorIdx;
    }
}
