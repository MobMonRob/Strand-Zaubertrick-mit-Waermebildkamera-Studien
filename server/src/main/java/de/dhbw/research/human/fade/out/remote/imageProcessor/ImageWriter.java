package de.dhbw.research.human.fade.out.remote.imageProcessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageWriter {
    public static void write(BufferedImage image) {
        try {
            ImageIO.write(image, "jpeg", new File("test.jpg"));
        } catch (IOException e) {
            System.out.println("Could not save image.");
        }
    }
}
