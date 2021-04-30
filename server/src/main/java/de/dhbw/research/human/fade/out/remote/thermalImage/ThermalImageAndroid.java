package de.dhbw.research.human.fade.out.remote.thermalImage;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// Size 2.457.784 Byte
// Image dimensions must be multiple of 8
public class ThermalImageAndroid extends ThermalImage {

    private Bitmap bitmap;

    public ThermalImageAndroid(Bitmap bitmap, int[] thermalData, TemperatureRange range, byte mode) {
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.mode = mode;
        this.bitmap = bitmap;
        this.thermalMask = ThermalDataEncoder.encode(thermalData, range);
    }

    void sendImage(DataOutputStream outputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        outputStream.writeInt(byteArrayOutputStream.size());
        byteArrayOutputStream.writeTo(outputStream);
    }
}
