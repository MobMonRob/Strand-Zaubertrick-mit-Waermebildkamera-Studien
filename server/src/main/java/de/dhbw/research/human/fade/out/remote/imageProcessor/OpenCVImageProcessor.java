package de.dhbw.research.human.fade.out.remote.imageProcessor;

import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;
import de.dhbw.research.human.fade.out.remote.ui.PreviewFrame;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

public class OpenCVImageProcessor implements ImageProcessor{

    private final PreviewFrame previewFrame;

    public OpenCVImageProcessor() {
        previewFrame = new PreviewFrame();
        previewFrame.setVisible(true);
    }

    @Override
    public void onImageReceived(ThermalImage thermalImage) {
        Mat mat = new Mat(thermalImage.getHeight(), thermalImage.getWidth(),
                          CvType.CV_8UC3);

        mat.put(0, 0, ((DataBufferByte) thermalImage.getBufferedImage().getRaster().
                getDataBuffer()).getData());

        Mat mask = new Mat(thermalImage.getHeight(), thermalImage.getWidth(), CvType.CV_8UC1);

        int[] thermalData = thermalImage.getThermalData();

        ColorModel colorModel = thermalImage.getBufferedImage().getColorModel();
        WritableRaster raster = thermalImage.getBufferedImage().copyData(null);
        boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
        BufferedImage maskImage = new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
        for (int y = 0; y < thermalImage.getHeight(); y++) {
            for (int x = 0; x < thermalImage.getWidth(); x++) {
                if (thermalData[x + y * thermalImage.getWidth()] > 30065) {
                    mask.put(y, x, 0xff);
                    maskImage.setRGB(x, y, 0xffffff);
                } else {
                    mask.put(y, x, 0x00);
                }
            }
        }

        List<MatOfPoint> contour = new ArrayList<>();
        Imgproc.findContours(mask, contour, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        Imgproc.drawContours(mask, contour, -1, new Scalar(255), 40);

        Photo.inpaint(mat, mask, mat, 5, Photo.INPAINT_NS);
//        mat.copyTo(mask, mat);

        BufferedImage result = new BufferedImage(thermalImage.getWidth(), thermalImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        byte[] data = ((DataBufferByte) result.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);

        previewFrame.updatePreview(thermalImage.getBufferedImage(), maskImage, result);
    }
}
