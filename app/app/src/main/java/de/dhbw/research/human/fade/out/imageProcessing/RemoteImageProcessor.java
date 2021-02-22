package de.dhbw.research.human.fade.out.imageProcessing;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.flir.flironesdk.RenderedImage;

import de.dhbw.research.human.fade.out.R;
import de.dhbw.research.human.fade.out.remote.client.AndroidClient;
import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;

public class RemoteImageProcessor implements ImageProcessor {

    private AndroidClient client;
    private ImageView imageView;
    private Bitmap lastVisualImage;


    public RemoteImageProcessor(final ImageView imageView, final Activity activity) {
        this.imageView = imageView;
        client = new AndroidClient("192.168.43.149", 4444);
        client.startConnection();

        SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.settings_file), Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(activity.getString(R.string.temperature_value_key))) {
                    client.setMaskTemperature(sharedPreferences.getInt(key, 30065));
                }
            }
        });
    }

    @Override
    public void init() {

    }

    @Override
    public void processImage(final RenderedImage image) {
        if (image.imageType() == RenderedImage.ImageType.VisibleAlignedRGBA8888Image) {
            lastVisualImage = image.getBitmap();
        } else if (image.imageType() == RenderedImage.ImageType.ThermalRadiometricKelvinImage) {
            ThermalImage thermalImage = new ThermalImage(lastVisualImage, image.thermalPixelValues());
            client.sendImage(thermalImage, true);
        }
    }
}
