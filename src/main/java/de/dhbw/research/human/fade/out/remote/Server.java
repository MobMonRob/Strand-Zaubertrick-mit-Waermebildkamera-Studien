package de.dhbw.research.human.fade.out.remote;

import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.graphics.Bitmap;
import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;

import javax.swing.*;

public class Server {

    private int port;

    private PreviewFrame previewFrame;

    private boolean hasConnection;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectInputStream inputStream;
//    private ObjectOutputStream outputStream;

    public Server(int port) {
        this.port = port;
        previewFrame = new PreviewFrame();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();

//            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            inputStream = new ObjectInputStream(clientSocket.getInputStream());

            previewFrame.setVisible(true);
            hasConnection = true;
            while (hasConnection) {
                try {
                    ThermalImage nextImage = (ThermalImage) inputStream.readObject();
                    System.out.println("Received image");
                    BufferedImage bufferedImage = toImage(nextImage);
                    previewFrame.updatePreview(bufferedImage);
//                this.send(nextImage);
                } catch (EOFException e) {
                    System.out.println("Connection closed by client");
                    hasConnection = false;
                    this.stop();
                }
            }
        } catch (IOException e) {
            System.out.println("Error while starting server:");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            inputStream.close();
//            outputStream.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error while closing server:");
            e.printStackTrace();
        }
    }

//    public void send(Bitmap image) {
//        image.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
//    }

    public BufferedImage toImage(ThermalImage image) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getVisualData()[x + y * image.getWidth()] & 0xffffff;
                bufferedImage.setRGB(x, y, rgb);
            }
        }
        return bufferedImage;
    }

    public static void main(String[] args) {


        Server server = new Server(4444);
        server.start();
    }
}
