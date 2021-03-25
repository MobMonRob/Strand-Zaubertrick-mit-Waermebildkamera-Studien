package de.dhbw.research.human.fade.out.remote.imageProcessor;

import com.google.common.primitives.Booleans;
import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;
import de.dhbw.research.human.fade.out.remote.ui.PreviewFrame;
import org.opencv.core.Size;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

public class CopyImageProcessor implements ImageProcessor {

    private int[] backgroundImage = new int[0];
    private double pollution = 1.0f;

    private final PreviewFrame previewFrame;
    private final VideoCreator videoCreator;

    private boolean recording = false;

    private static final int SKIP_COLOR = -16742656;

    public CopyImageProcessor() {
        previewFrame = new PreviewFrame();
        previewFrame.setVisible(true);
        videoCreator = new VideoCreator(10, new Size(480, 640));
    }

    @Override
    public void onImageReceived(ThermalImage image) {
        if (image.getBufferedImage().getRGB(0, 0) == SKIP_COLOR) {
            return;
        }

        int[] pixels = image.getBufferedImage()
                            .getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        updateBackgroundImage(pixels, image.getThermalMask(), image.shouldReset());

        replaceSections(pixels, image.getThermalMask(), 10);
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        result.setRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        previewFrame.updatePreview(image.getBufferedImage(), new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB), result);
        if (image.shouldTakePhoto()) {
            new Thread(() -> ImageWriter.write(result)).start();
        }
        if (image.shouldCapture()) {
            new Thread(() ->videoCreator.addFrame(result, !recording)).start();
            if (!recording) {
                recording = true;
            }
        }
        if (recording && !image.shouldCapture()) {
            recording = false;
        }
    }

    private void updateBackgroundImage(int[] image, boolean[] mask, boolean reset) {
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

    private double calculatePollution(boolean[] mask) {
        long thermalCount = Booleans.asList(mask)
                                    .stream()
                                    .filter(Boolean::booleanValue)
                                    .count();
        return thermalCount / (double) mask.length;
    }

    private void replaceSections(int[] image, boolean[] mask, int threadCount) {
        int sectionSize = image.length / threadCount;
        IntStream.range(0, threadCount)
                 .mapToObj(i -> new Thread(() -> replaceSection(sectionSize * i, sectionSize * (i + 1), image, mask)))
                 .peek(Thread::start)
                 .forEach(thread -> {
                     try {
                         thread.join();
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                 });
    }

    private void replaceSection(int start, int end, int[] image, boolean[] mask) {
        for (int i = start; i < end; i++) {
            if (mask[i]) {
                image[i] = backgroundImage[i];
            }
        }
    }
}
