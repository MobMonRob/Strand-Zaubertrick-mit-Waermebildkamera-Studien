package de.dhbw.research.human.fade.out.remote;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PreviewFrame extends JFrame {

    private BufferedImage previewImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

    public PreviewFrame() {
        super();

        final JLabel fpsLabel = new JLabel("00");
        fpsLabel.setForeground(Color.RED);
        this.add(fpsLabel);

        this.setTitle("Live Image");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(480 , 640);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(previewImage, 0, 0, null);
        super.paint(g);
    }

    public void updatePreview(BufferedImage previewImage) {
        this.previewImage = previewImage;
        this.setSize(previewImage.getWidth(), previewImage.getHeight());
        this.repaint();
    }
}
