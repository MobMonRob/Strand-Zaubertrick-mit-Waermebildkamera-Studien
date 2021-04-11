package de.dhbw.research.human.fade.out.remote.imageProcessor;

import de.dhbw.research.human.fade.out.remote.thermalImage.ThermalImageJava;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaptureImageProcessor implements ImageProcessor{

    private final DataOutputStream outputStream;

    public CaptureImageProcessor(String path) throws FileNotFoundException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd:MM:yyyy-HH:mm:ss");
        Date date = new Date();
        outputStream = new DataOutputStream(new FileOutputStream(path + "-" + formatter.format(date)));
    }

    @Override
    public void onImageReceived(ThermalImageJava image) {
        try {
            image.send(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionClosed() {
    }
}
