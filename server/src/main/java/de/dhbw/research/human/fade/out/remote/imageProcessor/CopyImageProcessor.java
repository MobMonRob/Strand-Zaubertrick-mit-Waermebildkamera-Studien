package de.dhbw.research.human.fade.out.remote.imageProcessor;

import de.dhbw.research.human.fade.out.remote.imageProcessor.util.ImageWriter;
import de.dhbw.research.human.fade.out.remote.imageProcessor.util.VideoCreator;
import de.dhbw.research.human.fade.out.remote.thermalImage.ThermalImageJava;
import de.dhbw.research.human.fade.out.remote.ui.PreviewFrame;
import org.opencv.core.Size;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CopyImageProcessor implements ImageProcessor {

    private int[] backgroundImage = new int[0];
    private double pollution = 1.0f;

    private final PreviewFrame previewFrame;
    private final VideoCreator videoCreator;

    private boolean recording = false;

    private static final int SKIP_COLOR = -16742656;
    private static final int THREAD_COUNT = 10;
    private static final int RADIUS = 10;

    public CopyImageProcessor() {
        previewFrame = new PreviewFrame();
        previewFrame.setVisible(true);
        videoCreator = new VideoCreator(5, new Size(480, 640));
    }

    @Override
    public void onImageReceived(ThermalImageJava image) {
        if (image.getBufferedImage().getRGB(0, 0) == SKIP_COLOR) {
            return;
        }

        int[] pixels = image.getBufferedImage()
                            .getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        updateBackgroundImage(pixels, image.getBooleanThermalMask(), image.shouldReset());


        replaceSections(pixels, image.getBooleanThermalMask(), image.getWidth());
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        result.setRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        int[] maskData = Arrays.stream(image.getBooleanThermalMask()).mapToInt(value -> value ? 0x00ffffff : 0).toArray();
        BufferedImage mask = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        mask.setRGB(0, 0, image.getWidth(), image.getHeight(), maskData, 0, image.getWidth());

        previewFrame.updatePreview(image.getBufferedImage(), mask, result);

        if (image.shouldTakePhoto()) {
            ImageWriter.write(result);
        }
        if (image.shouldCapture()) {
            videoCreator.addFrame(result, !recording);
            if (!recording) {
                recording = true;
            }
        }
        if (recording && !image.shouldCapture()) {
            videoCreator.save();
            recording = false;
        }
    }

    private void updateBackgroundImage(int[] image, Boolean[] mask, boolean reset) {
        double newPollution = calculatePollution(mask);
        if (backgroundImage.length == 0 || newPollution <= pollution || reset) {
            backgroundImage = image;
            if (newPollution > 0.00001f) {
                pollution = newPollution;
            }
        } else {
            pollution += 0.00005f;
        }
    }

    private double calculatePollution(Boolean[] mask) {
        long thermalCount = Arrays.stream(mask)
                                  .filter(Boolean::booleanValue)
                                  .count();
        return thermalCount / (double) mask.length;
    }

    private void replaceSections(int[] image, Boolean[] mask, int width) {
        int sectionSize = image.length / THREAD_COUNT;
        List<Thread> threads = IntStream.range(0, THREAD_COUNT)
                                        .mapToObj(i -> new Thread(() -> replaceSection(sectionSize * i, sectionSize * (i + 1), image, mask, width)))
                                        .collect(Collectors.toList());
        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void replaceSection(int start, int end, int[] image, Boolean[] mask, int width) {
        for (int i = start; i < end; i++) {
            if (checkWithRadius(i, mask, width)) {
                image[i] = backgroundImage[i];
            }
        }
    }

    private boolean checkWithRadius(int pos, Boolean[] mask, int width) {
        for (int x = -RADIUS; x < RADIUS; x++) {
            for (int y = -RADIUS + Math.abs(x); y < RADIUS - Math.abs(x); y++) {
                int index = pos + x + y * width;
                index = index < 0 ? index + mask.length : index % mask.length;
                if (mask[index]) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onConnectionClosed() {
        videoCreator.save();
        recording = false;
    }
}
