package de.dhbw.research.human.fade.out.remote.imageProcessor.util;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VideoCreator {

    private final double fps;
    private final Size size;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH:mm:ss");

    private final VideoWriter videoWriter;
    private final Queue<Runnable> actionQueue = new ConcurrentLinkedQueue<>();

    private static final int FOURCC = VideoWriter.fourcc('m', 'p', '4', 'v');

    public VideoCreator(double fps, Size size) {
        this.fps = fps;
        this.size = size;
        videoWriter = new VideoWriter();

        Thread writerThread = new Thread(() -> {
            while (true) {
                try {
                    performActions();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        writerThread.start();
    }

    public void addFrame(Mat image, boolean newVideo) {
        synchronized (actionQueue) {
            actionQueue.add(() -> writeFrame(image, newVideo));
            actionQueue.notify();
        }
    }

    public void addFrame(BufferedImage image, boolean newVideo) {
        synchronized (actionQueue) {
            actionQueue.add(() -> {
                BufferedImage bgrImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                bgrImage.getGraphics().drawImage(image, 0, 0, null);
                bgrImage.getGraphics().dispose();

                Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
                byte[] data = ((DataBufferByte) bgrImage.getRaster().getDataBuffer()).getData();

                mat.put(0, 0, data);
                writeFrame(mat, newVideo);
            });
            actionQueue.notify();
        }
    }

    public void save() {
        synchronized (actionQueue) {
            actionQueue.add(videoWriter::release);
            actionQueue.notify();
        }
    }

    private void performActions() throws InterruptedException {
        synchronized (actionQueue) {
            while (actionQueue.isEmpty()) {
                actionQueue.wait();
            }
            Runnable action = actionQueue.poll();
            action.run();
        }
    }

    private void writeFrame(Mat image, boolean newVideo) {
        if (newVideo) {
            String filename = "./video-" + formatter.format(LocalDateTime.now()) + ".mp4";
            videoWriter.open(filename, FOURCC, fps, size);
        }
        videoWriter.write(image);
    }
}
