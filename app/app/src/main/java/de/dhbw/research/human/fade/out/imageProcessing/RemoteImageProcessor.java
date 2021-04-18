package de.dhbw.research.human.fade.out.imageProcessing;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.flir.flironesdk.RenderedImage;

import de.dhbw.research.human.fade.out.R;
import de.dhbw.research.human.fade.out.remote.client.AndroidClient;
import de.dhbw.research.human.fade.out.remote.thermalImage.TemperatureRange;
import de.dhbw.research.human.fade.out.remote.thermalImage.ThermalImage;
import de.dhbw.research.human.fade.out.remote.thermalImage.ThermalImageAndroid;

public class RemoteImageProcessor implements ImageProcessor, SharedPreferences.OnSharedPreferenceChangeListener {

    private AndroidClient client;
    private ImageView imageView;
    private Bitmap lastVisualImage;
    private SharedPreferences sharedPreferences;
    private Activity activity;

    private TemperatureRange range;

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
        updateRange();

        client = new AndroidClient(ip, port);
        client.startConnection();

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void processImage(final RenderedImage image) {
        if (image.imageType() == RenderedImage.ImageType.VisibleAlignedRGBA8888Image) {
            lastVisualImage = image.getBitmap();
        } else if (image.imageType() == RenderedImage.ImageType.ThermalRadiometricKelvinImage) {

            ThermalImageAndroid thermalImage = new ThermalImageAndroid(lastVisualImage, image.thermalPixelValues(), range, generateMode());
            client.sendImage(thermalImage, true);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(activity.getString(R.string.lower_temperature_value_key))) {
            updateRange();
        } else if (key.equals(activity.getString(R.string.upper_temperature_value_key))) {
            updateRange();
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

    private void updateRange() {
        int minTemperature = sharedPreferences.getInt(activity.getString(R.string.lower_temperature_value_key), 30065);
        int maxTemperature = sharedPreferences.getInt(activity.getString(R.string.upper_temperature_value_key), 30085);

        range = new TemperatureRange(minTemperature, maxTemperature);
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
