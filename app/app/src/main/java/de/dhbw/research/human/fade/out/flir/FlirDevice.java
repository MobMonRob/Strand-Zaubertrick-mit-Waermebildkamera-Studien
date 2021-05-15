package de.dhbw.research.human.fade.out.flir;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.flir.flironesdk.Device;
import com.flir.flironesdk.Frame;
import com.flir.flironesdk.FrameProcessor;
import com.flir.flironesdk.RenderedImage;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;

import de.dhbw.research.human.fade.out.BuildConfig;
import de.dhbw.research.human.fade.out.R;
import de.dhbw.research.human.fade.out.imageProcessing.ImageProcessor;
import de.dhbw.research.human.fade.out.imageProcessing.RemoteImageProcessor;

public class FlirDevice implements Device.Delegate, Device.StreamDelegate, Device.PowerUpdateDelegate, FrameProcessor.Delegate {
    private Activity activity;
    private FrameProcessor frameProcessor;
    private Device.TuningState tuningState = Device.TuningState.Unknown;
    private Device flirDevice = null;
    private ImageProcessor imageProcessor;

    private Instant lastFrameReceived = Instant.now();
    private Instant lastFrameProcessed = Instant.now();


    public FlirDevice(Activity activity) {
        this.activity = activity;
        this.frameProcessor = new FrameProcessor(activity, this, EnumSet.of(RenderedImage.ImageType.ThermalRadiometricKelvinImage, RenderedImage.ImageType.VisibleAlignedRGBA8888Image));
        imageProcessor = new RemoteImageProcessor();
    }

    public void start() {
        try {
            if (flirDevice != null) {
                flirDevice.setPowerUpdateDelegate(this);
                flirDevice.startFrameStream(this);

                imageProcessor.init((ImageView) activity.findViewById(R.id.image), activity);
            } else {
                Device.startDiscovery(activity, this);
            }
        } catch (final Exception e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void stop() {
        try {
            flirDevice.stopFrameStream();
            Device.stopDiscovery();
        } catch (Exception e) { /* Do nothing */ }

        flirDevice = null;
    }

    @Override
    public void onDeviceDisconnected(Device device) {
        flirDevice = null;
    }

    @Override
    public void onTuningStateChanged(Device.TuningState tuningState) {
        if (tuningState != null) {
            this.tuningState = tuningState;
        }
    }

    @Override
    public void onDeviceConnected(Device device) {
        if (device != null) {
            flirDevice = device;
            start();
        }
    }

    @Override
    public void onAutomaticTuningChanged(boolean changed) {
        // Do nothing
    }

    @Override
    public void onFrameReceived(Frame frame) {
        if (tuningState == Device.TuningState.Tuned || tuningState == Device.TuningState.ApproximatelyTuned) {
            frameProcessor.processFrame(frame, FrameProcessor.QueuingOption.CLEAR_QUEUED);
            if (BuildConfig.DEBUG) {
                Instant currentReceived = Instant.now();
                long elapsedMilliseconds = Duration.between(lastFrameReceived, currentReceived).toMillis();
                Log.d("TIME-CAPTURE-FRAME", "Time - Receive: " + (1000F / elapsedMilliseconds) + " FPS, " + elapsedMilliseconds + " ms");
                lastFrameReceived = currentReceived;
            }

        }
    }

    @Override
    public void onBatteryPercentageReceived(byte value) {
        // Do nothing
    }

    @Override
    public void onBatteryChargingStateReceived(Device.BatteryChargingState state) {
        // Do nothing
    }

    @Override
    public void onFrameProcessed(final RenderedImage renderedImage) {
        imageProcessor.processImage(renderedImage);
        if (BuildConfig.DEBUG) {
            Instant currentProcessed = Instant.now();
            long elapsedMilliseconds = Duration.between(lastFrameProcessed, currentProcessed).toMillis();
            Log.d("TIME-PROCESS-FRAME", "Time - Receive: " + (1000F / elapsedMilliseconds) + " FPS, " + elapsedMilliseconds + " ms");
            lastFrameProcessed = currentProcessed;
        }
    }
}
