package de.dhbw.research.human.fade.out.remote;

import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private int port;

    private PreviewFrame previewFrame;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectInputStream inputStream;

    public Server(int port) {
        this.port = port;
        previewFrame = new PreviewFrame();
        previewFrame.setVisible(true);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {

                clientSocket = serverSocket.accept();

                inputStream = new ObjectInputStream(clientSocket.getInputStream());

                boolean hasConnection = true;
                while (hasConnection) {
                    try {
                        onImageReceived((ThermalImage) inputStream.readUnshared());
                    } catch (EOFException e) {
                        System.out.println("Connection closed by client");
                        hasConnection = false;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error while starting server:");
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            inputStream.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error while closing server:");
            e.printStackTrace();
        }
    }

    private void onImageReceived(ThermalImage image) {
        Mat mat = new Mat(image.getHeight(), image.getWidth(),
                          CvType.CV_8UC3);

        mat.put(0, 0, ((DataBufferByte) image.getBufferedImage().getRaster().
                getDataBuffer()).getData());

        Mat mask = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC1);

        int[] thermalData = image.getThermalData();

        ColorModel colorModel = image.getBufferedImage().getColorModel();
        WritableRaster raster = image.getBufferedImage().copyData(null);
        boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
        BufferedImage maskImage = new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (thermalData[x + y * image.getWidth()] > 30315) {
                    mask.put(y, x, 0xff);
                    maskImage.setRGB(x, y, 0xffffff);
                } else {
                    mask.put(y, x, 0x00);
                }
            }
        }

        List<MatOfPoint> contour = new ArrayList<>();
        Imgproc.findContours(mask, contour, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        Imgproc.drawContours(mask, contour, -1, new Scalar(255), 40);

        Photo.inpaint(mat, mask, mat, 5, Photo.INPAINT_NS);
//        mat.copyTo(mask, mat);

        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        byte[] data = ((DataBufferByte) result.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);

        previewFrame.updatePreview(image.getBufferedImage(), maskImage, result);
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadShared();
        Server server = new Server(4444);
        server.start();
    }
}
