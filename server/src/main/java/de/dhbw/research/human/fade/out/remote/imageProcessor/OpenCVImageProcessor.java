package de.dhbw.research.human.fade.out.remote.imageProcessor;

import com.google.common.primitives.Bytes;
import de.dhbw.research.human.fade.out.remote.imageProcessor.util.ImageWriter;
import de.dhbw.research.human.fade.out.remote.imageProcessor.util.VideoCreator;
import de.dhbw.research.human.fade.out.remote.thermalImage.ThermalImageJava;
import de.dhbw.research.human.fade.out.remote.ui.PreviewFrame;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OpenCVImageProcessor implements ImageProcessor {

    private final PreviewFrame previewFrame;
    private final VideoCreator videoCreator;

    private boolean recording = false;

    public OpenCVImageProcessor() {
        previewFrame = new PreviewFrame();
        previewFrame.setVisible(true);
        videoCreator = new VideoCreator(5, new Size(480, 640));
    }

    @Override
    public void onImageReceived(ThermalImageJava image) {
        Mat mat = new Mat(image.getHeight(), image.getWidth(),
                          CvType.CV_8UC3);

        mat.put(0, 0, ((DataBufferByte) image.getBufferedImage().getRaster().
                getDataBuffer()).getData());


        byte[] maskData = Bytes.toArray(Arrays.stream(image.getBooleanThermalMask())
                                              .map(value -> (byte) (value ? 255 : 0))
                                              .collect(Collectors.toList()));

        Mat mask = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC1);
        mask.put(0, 0, maskData);

        List<MatOfPoint> contour = new ArrayList<>();
        Imgproc.findContours(mask, contour, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        Imgproc.drawContours(mask, contour, -1, new Scalar(255), 40);

        Photo.inpaint(mat, mask, mat, 1, Photo.INPAINT_NS);
//        mat.copyTo(mask, mat);

        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        byte[] data = ((DataBufferByte) result.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);

        int[] maskDataImage = Arrays.stream(image.getBooleanThermalMask()).mapToInt(value -> value ? 0x00ffffff : 0).toArray();
        BufferedImage maskImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        maskImage.setRGB(0, 0, image.getWidth(), image.getHeight(), maskDataImage, 0, image.getWidth());

        previewFrame.updatePreview(image.getBufferedImage(), maskImage, result);

        if (image.shouldTakePhoto()) {
            ImageWriter.write(result);
        }
        if (image.shouldCapture()) {
            videoCreator.addFrame(mat, !recording);
            if (!recording) {
                recording = true;
            }
        }
        if (recording && !image.shouldCapture()) {
            videoCreator.save();
            recording = false;
        }
    }

    @Override
    public void onConnectionClosed() {
        videoCreator.save();
        recording = false;
    }
}
