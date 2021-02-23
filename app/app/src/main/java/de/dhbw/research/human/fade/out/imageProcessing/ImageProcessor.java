package de.dhbw.research.human.fade.out.imageProcessing;

import android.app.Activity;
import android.widget.ImageView;

import com.flir.flironesdk.RenderedImage;

public interface ImageProcessor  {

    void init(final ImageView imageView, final Activity activity);

    void processImage(RenderedImage image);
}
