package de.dhbw.research.human.fade.out.imageProcessing;

import android.widget.ImageView;

import com.flir.flironesdk.RenderedImage;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PlainImageProcessor implements ImageProcessor{

    private ImageView imageView;

    private Queue<Runnable> renderTasks = new ConcurrentLinkedQueue<>();

    public PlainImageProcessor(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!renderTasks.isEmpty()) {
                        imageView.post(renderTasks.poll());
                    }
                }
            }
        }).start();
    }

    @Override
    public void processImage(final RenderedImage image) {
        if (image != null && image.imageType() == RenderedImage.ImageType.VisibleAlignedRGBA8888Image) {
            renderTasks.clear();
            renderTasks.add(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(image.getBitmap());
                }
            });
        }
    }
}
