package de.dhbw.research.human.fade.out.remote;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PreviewFrame extends JFrame {

    private BufferedImage previewImage = new BufferedImage(1,1, BufferedImage.TYPE_INT_RGB);

    public PreviewFrame() {
        super();
        this.setTitle("Live Image");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(previewImage, 0, 0, null);
    }

    public void updatePreview(BufferedImage previewImage) {
        this.previewImage = previewImage;
        this.repaint();
        this.setSize(previewImage.getWidth(), previewImage.getHeight());
    }
}
