package com.bawnorton.trimica.client.texture.palette;

import com.bawnorton.trimica.client.texture.colour.OkLabHelper;
import com.mojang.blaze3d.platform.NativeImage;
import javax.imageio.ImageIO;
import net.minecraft.Util;
import net.minecraft.util.ARGB;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TrimPalette {
    public static final TrimPalette DEFAULT = new TrimPalette(ARGB.color(255, 255, 255, 255));
    public static final int PALETTE_SIZE = 8;
    private final List<Integer> colours;

    public TrimPalette(List<Integer> colours) {
        if (colours.size() != PALETTE_SIZE) {
            throw new IllegalArgumentException("Trim palette requires exactly %s colours, but %s were found.".formatted(PALETTE_SIZE, colours.size()));
        }
        this.colours = colours;
    }

    public TrimPalette(int singleColour) {
        this(Util.make(new ArrayList<>(), colours -> {
            for (int i = 0; i < PALETTE_SIZE; i++) {
                colours.add(singleColour);
            }
        }));
    }

    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(PALETTE_SIZE, 1, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < PALETTE_SIZE; i++) {
            image.setRGB(i, 0, (255 << 24) | colours.get(i));
        }
        return image;
    }

    public InputStream toInputStream() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            BufferedImage bufferedImage = toBufferedImage();
            ImageIO.write(bufferedImage, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public List<Integer> getColours() {
        return colours;
    }

    public int getAverageColour() {
        double[][] okLabSpace = new double[PALETTE_SIZE][3];
        for (int i = 0; i < PALETTE_SIZE; i++) {
            int colour = colours.get(i);
            double[] okLab = OkLabHelper.rgbToOklab(colour);
            okLabSpace[i] = okLab;
        }
        double[] averaged = OkLabHelper.average(okLabSpace);
        return OkLabHelper.oklabToRgb(averaged);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TrimPalette other) {
            return Objects.equals(colours, other.colours);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(colours);
    }
}
