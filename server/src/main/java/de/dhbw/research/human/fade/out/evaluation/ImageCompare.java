package de.dhbw.research.human.fade.out.evaluation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCompare {


    public static void compareImages(int[] referenceImage, int[] image) {
        ImageDifference difference = new ImageDifference();
        int[] diffData = new int[referenceImage.length];

        for (int i = 0; i < referenceImage.length; i++) {
            PixelProperties properties = new PixelProperties(referenceImage[i], image[i]);
            difference.update(properties);
            if (difference.getPixelsOffInPercent()>10) {
                diffData[i] = 0xff0000;
            }else {
                diffData[i] = 0x0000ff;
            }
        }
        BufferedImage diffImg = new BufferedImage(480, 640, BufferedImage.TYPE_INT_RGB);
        diffImg.setRGB(0, 0, 480, 640, diffData, 0, 480);
        System.out.println(difference.getPixelsOffInPercent() +
                           " - " +
                           difference.getPixelsOff5InPercent() +
                           " - " +
                           difference.getPixelsOff10InPercent());
    }

    public static void main(String[] args) throws IOException {

        BufferedImage refImage = ImageIO.read(new File("./test-background1.jpg"));
        int[] refData = refImage.getRGB(0, 0, refImage.getWidth(), refImage.getHeight(), null, 0, refImage.getWidth());

        BufferedImage refImage2 = ImageIO.read(new File("./test-background2.jpg"));
        int[] refData2 = refImage2.getRGB(0, 0, refImage2.getWidth(), refImage2.getHeight(), null, 0, refImage2.getWidth());

        File dir = new File("./ai");
        for (int i = 0; i < dir.list().length; i++) {
            BufferedImage image = ImageIO.read(new File(dir.getAbsolutePath() + "/image-" + i + ".jpg"));
            int[] data = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

            if (i >= 0 && i <= 20) {
                compareImages(refData, data);
            } else if (i >= 21 && i <= 51) {
                compareImages(refData2, data);
            } else if (i >= 52 && i < 82) {
                compareImages(refData, data);
            } else if (i >= 82 && i <= 107) {
                compareImages(refData2, data);
            } else {
                compareImages(refData, data);
            }
        }
    }
}
