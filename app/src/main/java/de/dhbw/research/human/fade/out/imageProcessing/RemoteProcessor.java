package de.dhbw.research.human.fade.out.imageProcessing;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.flir.flironesdk.RenderedImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import de.dhbw.research.human.fade.out.remote.AsyncClient;
import de.dhbw.research.human.fade.out.remote.SimpleClient;
import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;

public class RemoteProcessor implements ImageProcessor {

    private AsyncClient client;
    private ImageView imageView;
    private int[] lastVisualPixels;

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
            Bitmap bitmap = image.getBitmap();
            lastVisualPixels = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(lastVisualPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        } else if (image.imageType() == RenderedImage.ImageType.ThermalRadiometricKelvinImage) {
            ThermalImage thermalImage = new ThermalImage(image.width(), image.height(), image.thermalPixelValues(), lastVisualPixels);
//            try {
//                sizeof(thermalImage);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            client.sendImage(thermalImage, true);
        }
    }

    private int sizeof(Object obj) throws IOException {

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        objectOutputStream.close();

        return byteOutputStream.toByteArray().length;
    }
}

