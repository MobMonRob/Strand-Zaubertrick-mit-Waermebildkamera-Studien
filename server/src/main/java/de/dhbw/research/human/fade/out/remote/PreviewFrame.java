package de.dhbw.research.human.fade.out.remote;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PreviewFrame extends JFrame {

    private ImagePanel originalImagePanel;
    private ImagePanel maskImagePanel;
    private ImagePanel inpaintedImagePanel;

    public PreviewFrame() {
        super();

//        final JLabel fpsLabel = new JLabel("00");
//        fpsLabel.setForeground(Color.RED);
//        this.add(fpsLabel);

        this.setLayout(new FlowLayout());

        originalImagePanel = new ImagePanel();
        this.add(originalImagePanel);
        maskImagePanel = new ImagePanel();
        this.add(maskImagePanel);
        inpaintedImagePanel = new ImagePanel();
        this.add(inpaintedImagePanel);

        this.setTitle("Live Image");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.pack();
    }

    public void updatePreview(BufferedImage originalImage, BufferedImage maskImage, BufferedImage inpaintedImage) {
        originalImagePanel.updateImage(originalImage);
        maskImagePanel.updateImage(maskImage);
        inpaintedImagePanel.updateImage(inpaintedImage);
        this.repaint();
    }
}
