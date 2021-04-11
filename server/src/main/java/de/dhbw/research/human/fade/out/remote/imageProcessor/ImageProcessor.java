package de.dhbw.research.human.fade.out.remote.imageProcessor;

import de.dhbw.research.human.fade.out.remote.thermalImage.ThermalImageJava;

public interface ImageProcessor {
    void onImageReceived(ThermalImageJava image);

    void onConnectionClosed();
}
