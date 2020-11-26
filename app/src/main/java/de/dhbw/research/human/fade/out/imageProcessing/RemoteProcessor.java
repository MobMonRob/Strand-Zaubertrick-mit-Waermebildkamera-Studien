package de.dhbw.research.human.fade.out.imageProcessing;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.flir.flironesdk.RenderedImage;

import de.dhbw.research.human.fade.out.remote.AsyncClient;
import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;

public class RemoteProcessor implements ImageProcessor {

    private AsyncClient client;
    private ImageView imageView;
    private Bitmap lastVisualImage;
    private ThermalImage thermalImage;


    public RemoteProcessor(final ImageView imageView) {
        this.imageView = imageView;
        client = new AsyncClient("192.168.43.149", 4444);
        client.startConnection();
    }

    @Override
    public void init() {

    }

    @Override
    public void processImage(final RenderedImage image) {
        if (image.imageType() == RenderedImage.ImageType.VisibleAlignedRGBA8888Image) {
            lastVisualImage = image.getBitmap();
        } else if (image.imageType() == RenderedImage.ImageType.ThermalRadiometricKelvinImage) {
            thermalImage = new ThermalImage(image.width(), image.height(), image.thermalPixelValues(), lastVisualImage);
            client.sendImage(thermalImage, true);
        }
    }
}
