package de.dhbw.research.human.fade.out.remote.imageProcessor;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoCreator {

    private final double fps;
    private final Size size;
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd:MM:yyyy-HH:mm:ss");

    VideoWriter videoWriter;

    public VideoCreator(double fps, Size size) {
        this.fps = fps;
        this.size = size;
    }

    public void addFrame(Mat image, boolean newVideo) {
        if (newVideo) {
            videoWriter = new VideoWriter("video-" + formatter.format(new Date()) + ".mp4", 0, fps, size);
        }
        videoWriter.write(image);
    }

    public void addFrame(BufferedImage image, boolean newVideo) {
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());
        addFrame(mat, newVideo);
    }
}
