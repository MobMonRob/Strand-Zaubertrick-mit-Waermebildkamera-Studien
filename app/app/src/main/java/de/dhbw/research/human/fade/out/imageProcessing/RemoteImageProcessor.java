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
    private Activity activity;

    private boolean reset = false;
    private boolean captureVideo = false;
    private boolean takePhoto = false;


    public RemoteImageProcessor() {
    }

    @Override
    public void init(final ImageView imageView, final Activity activity) {
        this.imageView = imageView;
        this.activity = activity;

        sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.settings_file), Context.MODE_PRIVATE);
        String ip = sharedPreferences.getString(activity.getString(R.string.server_ip_key), null);
        int port = sharedPreferences.getInt(activity.getString(R.string.server_port_key), 4444);
        int temperature = sharedPreferences.getInt(activity.getString(R.string.lower_temperature_value_key), 30065);

        client = new AndroidClient(ip, port);
        client.setMaskTemperature(temperature);
        client.startConnection();

        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(activity.getString(R.string.lower_temperature_value_key))) {
                    client.setMaskTemperature(sharedPreferences.getInt(key, 30065));
                } else if
                (key.equals(activity.getString(R.string.reset))) {
                    reset = sharedPreferences.getBoolean(key, false);
                } else if
                (key.equals(activity.getString(R.string.capture_video))) {
                    captureVideo = sharedPreferences.getBoolean(key, false);
                } else if
                (key.equals(activity.getString(R.string.take_photo))) {
                    takePhoto = sharedPreferences.getBoolean(key, false);
                }
            }
        });
    }

    @Override
    public void processImage(final RenderedImage image) {
        if (image.imageType() == RenderedImage.ImageType.VisibleAlignedRGBA8888Image) {
            lastVisualImage = image.getBitmap();
        } else if (image.imageType() == RenderedImage.ImageType.ThermalRadiometricKelvinImage) {

            ThermalImage thermalImage = new ThermalImage(lastVisualImage, image.thermalPixelValues(), generateMode());
            client.sendImage(thermalImage, true);
        }
    }

    private byte generateMode() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        byte mode = ThermalImage.MODE_NONE;
        if (reset) {
            mode |= ThermalImage.MODE_RESET;
            editor.putBoolean(activity.getString(R.string.reset), false);
        }
        if (captureVideo) {
            mode |= ThermalImage.MODE_CAPTURE;
        }
        if (takePhoto) {
            mode |= ThermalImage.MODE_PHOTO;
            editor.putBoolean(activity.getString(R.string.take_photo), false);
        }
        editor.apply();
        return mode;
    }
}
