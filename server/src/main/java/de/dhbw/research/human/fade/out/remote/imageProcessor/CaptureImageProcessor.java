package de.dhbw.research.human.fade.out.remote.imageProcessor;

import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CaptureImageProcessor implements ImageProcessor{

    private final DataOutputStream outputStream;

    public CaptureImageProcessor() throws FileNotFoundException {
        outputStream = new DataOutputStream(new FileOutputStream("recorded/test"));
    }

    @Override
    public void onImageReceived(ThermalImage image) {
        try {
            image.send(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
