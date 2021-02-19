package de.dhbw.research.human.fade.out.remote.imageProcessor;

import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;

public interface ImageProcessor {
    void onImageReceived(ThermalImage image);
}
