package de.dhbw.research.human.fade.out.remote;

import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;

import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private int port;

    private PreviewFrame previewFrame;

    private boolean hasConnection;
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
            clientSocket = serverSocket.accept();

            inputStream = new ObjectInputStream(clientSocket.getInputStream());

            hasConnection = true;
            while (hasConnection) {
                try {

                    ThermalImage nextImage = (ThermalImage) inputStream.readObject();

                    BufferedImage bufferedImage = toImage(nextImage);
                    previewFrame.updatePreview(bufferedImage);

                } catch (EOFException e) {
                    System.out.println("Connection closed by client");
                    hasConnection = false;
                    this.stop();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
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
