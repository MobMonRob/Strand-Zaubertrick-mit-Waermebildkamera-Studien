package de.dhbw.research.human.fade.out.imageProcessing;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

import com.flir.flironesdk.RenderedImage;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MaskedImageProcessor implements ImageProcessor {

    private ImageView imageView;

    private Queue<Runnable> maskRenderTasks = new ConcurrentLinkedQueue<>();

    private Queue<Bitmap> lastRealImages = new ConcurrentLinkedQueue<>();

    public MaskedImageProcessor(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public void init() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    if (!backgroundRenderTasks.isEmpty()) {
//                        backgroundImage.post(backgroundRenderTasks.poll());
//                    }
//                }
//            }
//        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!maskRenderTasks.isEmpty()) {
//                        new Thread(maskRenderTasks.poll()).start();
                        maskRenderTasks.poll().run();
                    }
                }
            }
        }).start();
    }

    @Override
    public void processImage(final RenderedImage image) {
        if (image != null && image.imageType() == RenderedImage.ImageType.ThermalRadiometricKelvinImage) {
            final Bitmap bitmap = lastRealImages.poll();
            if (!maskRenderTasks.isEmpty()) {
                maskRenderTasks.clear();
                lastRealImages.clear();
            }
            maskRenderTasks.add(new Runnable() {
                @Override
                public void run() {
                    int[] values = image.thermalPixelValues();
//            int max = 0;
//            int min = Integer.MAX_VALUE;
//            for (int value : values) {
//                if (value > max) {
//                    max = value;
//                }
//                if (value < min) {
//                    min = value;
//                }
//            }

//            ((TextView) activity.findViewById(R.id.maxTemp)).setText((max / 100f - 273.15f) + " C");
//            ((TextView) activity.findViewById(R.id.minTemp)).setText((min / 100f - 273.15f) + " C");


//                    final Bitmap bitmap = Bitmap.createBitmap(image.width(), image.height(), Bitmap.Config.ARGB_8888);
                    for (int y = 0; y < image.height(); y++) {
                        for (int x = 0; x < image.width(); x++) {
                            if (values[x + y * image.width()] > 30415) {
                                bitmap.setPixel(x, y, Color.RED);
                            }

                        }
                    }

                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }
            });
        } else if (image != null && image.imageType() == RenderedImage.ImageType.VisibleAlignedRGBA8888Image) {
//            backgroundRenderTasks.clear();
//            backgroundRenderTasks.add(new Runnable() {
//                @Override
//                public void run() {
//                    backgroundImage.setImageBitmap(image.getBitmap());
//                }
//            });
            lastRealImages.add(image.getBitmap());
        }
    }
}
