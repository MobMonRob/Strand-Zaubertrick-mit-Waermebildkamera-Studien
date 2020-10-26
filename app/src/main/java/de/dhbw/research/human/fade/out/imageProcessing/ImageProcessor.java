package de.dhbw.research.human.fade.out.imageProcessing;

import com.flir.flironesdk.RenderedImage;

public interface ImageProcessor  {

    void init();

    void processImage(RenderedImage image);
}
