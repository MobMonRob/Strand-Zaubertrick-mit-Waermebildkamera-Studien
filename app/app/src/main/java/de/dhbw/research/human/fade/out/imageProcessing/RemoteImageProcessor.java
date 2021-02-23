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
    private SharedPreferences sharedPreferences;


    public RemoteImageProcessor() {
    }

    @Override
    public void init(final ImageView imageView, final Activity activity) {
        this.imageView = imageView;

        sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.settings_file), Context.MODE_PRIVATE);
        String ip = sharedPreferences.getString(activity.getString(R.string.server_ip_key), null);
        int port = sharedPreferences.getInt(activity.getString(R.string.server_port_key), 4444);
        int temperature = sharedPreferences.getInt(activity.getString(R.string.temperature_value_key), 30065);

        client = new AndroidClient(ip, port);
        client.setMaskTemperature(temperature);
        client.startConnection();

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
    public void processImage(final RenderedImage image) {
        if (image.imageType() == RenderedImage.ImageType.VisibleAlignedRGBA8888Image) {
            lastVisualImage = image.getBitmap();
        } else if (image.imageType() == RenderedImage.ImageType.ThermalRadiometricKelvinImage) {
            ThermalImage thermalImage = new ThermalImage(lastVisualImage, image.thermalPixelValues());
            client.sendImage(thermalImage, true);
        }
    }
}
