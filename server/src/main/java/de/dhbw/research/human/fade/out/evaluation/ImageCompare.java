package de.dhbw.research.human.fade.out.evaluation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCompare {


    public static void compareImages(int[] referenceImage, int[] mask, int[] image) {
        ImageDifference completeDifference = new ImageDifference();
        ImageDifference maskedDifference = new ImageDifference();
        ImageDifference nonMaskedDifference = new ImageDifference();

        for (int i = 0; i < referenceImage.length; i++) {
            PixelProperties properties = new PixelProperties(referenceImage[i], mask[i], image[i]);

            completeDifference.update(properties);
            if (properties.isMasked()) {
                maskedDifference.update(properties);
            } else {
                nonMaskedDifference.update(properties);
            }
        }
        System.out.println(completeDifference.getPixelsOffInPercent());
        System.out.println(completeDifference.getPixelsOff5InPercent());
        System.out.println(completeDifference.getPixelsOff10InPercent());
        System.out.println(completeDifference.getDifferingRGBInPercent());
        System.out.println(completeDifference.getDifferingHSV_HInPercent());
        System.out.println(completeDifference.getDifferingHSV_SInPercent());
        System.out.println(completeDifference.getDifferingHSV_VInPercent());
        System.out.println();
        System.out.println(maskedDifference.getPixelsOffInPercent());
        System.out.println(maskedDifference.getPixelsOff5InPercent());
        System.out.println(maskedDifference.getPixelsOff10InPercent());
        System.out.println(maskedDifference.getDifferingRGBInPercent());
        System.out.println(maskedDifference.getDifferingHSV_HInPercent());
        System.out.println(maskedDifference.getDifferingHSV_SInPercent());
        System.out.println(maskedDifference.getDifferingHSV_VInPercent());
        System.out.println();
        System.out.println(nonMaskedDifference.getPixelsOffInPercent());
        System.out.println(nonMaskedDifference.getPixelsOff5InPercent());
        System.out.println(nonMaskedDifference.getPixelsOff10InPercent());
        System.out.println(nonMaskedDifference.getDifferingRGBInPercent());
        System.out.println(nonMaskedDifference.getDifferingHSV_HInPercent());
        System.out.println(nonMaskedDifference.getDifferingHSV_SInPercent());
        System.out.println(nonMaskedDifference.getDifferingHSV_VInPercent());
    }

    public static void main(String[] args) throws IOException {

        BufferedImage refImage = ImageIO.read(new File("./images/example.jpg"));
        int[] refData = refImage.getRGB(0, 0, refImage.getWidth(), refImage.getHeight(), null, 0, refImage.getWidth());

        BufferedImage mask = ImageIO.read(new File("./images/example-mask3.jpg"));
        int[] maskData = mask.getRGB(0, 0, mask.getWidth(), mask.getHeight(), null, 0, mask.getWidth());

        BufferedImage image = ImageIO.read(new File("./images/example-modified3.jpg"));
        int[] data = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        compareImages(refData, maskData, data);
    }
}
