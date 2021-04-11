package de.dhbw.research.human.fade.out.remote.imageProcessor.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImageWriter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH:mm:ss");


    public static void write(BufferedImage image) {
        try {
            ImageIO.write(image, "jpeg", new File("image-" + formatter.format(LocalDateTime.now()) + ".jpg"));
        } catch (IOException e) {
            System.out.println("Could not save image.");
        }
    }
}
