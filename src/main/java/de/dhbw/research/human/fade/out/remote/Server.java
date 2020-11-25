package de.dhbw.research.human.fade.out.remote;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.Array;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import android.graphics.Bitmap;
import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;

import javax.swing.*;

public class Server {

    private int port;

    private PreviewFrame previewFrame;

    private boolean hasConnection;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedInputStream inputStream;
//    private ObjectOutputStream outputStream;

    public Server(int port) {
        this.port = port;
        previewFrame = new PreviewFrame();
        previewFrame.setVisible(true);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();

//            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            inputStream = new BufferedInputStream(clientSocket.getInputStream(), 480 * 640 * 6 + 4);

            hasConnection = true;
            int width, height;
            int[] thermalData, visualData;
            byte[] header = new byte[4];
            while (hasConnection) {
                try {
                    if (inputStream.read(header) != -1)
                    {
                        width = ByteBuffer.wrap(header, 0, 2).getShort();
                        height = ByteBuffer.wrap(header, 2, 2).getShort();

                        byte[] thermalDataBytes = new byte[width * height * 2];
                        byte[] visualDataBytes = new byte[width * height * 4];

                        for (int i = 0; i < thermalDataBytes.length; i++) {
                            thermalDataBytes[i] = (byte) inputStream.read();
                        }
                        for (int i = 0; i < visualDataBytes.length; i++) {
                            visualDataBytes[i] = (byte) inputStream.read();
                        }

//                        int receivedThermalData = inputStream.read(thermalDataBytes);
//                        int receivedVisualData = inputStream.read(visualDataBytes);

                        final ByteBuffer wrappedThermalData = ByteBuffer.wrap(thermalDataBytes);
                        final ByteBuffer wrappedVisualData = ByteBuffer.wrap(visualDataBytes);
                        thermalData = new int[width * height];
                        visualData = new int[width * height];
                        for (int i = 0; i < width * height; i++) {
                            thermalData[i] = wrappedThermalData.getShort();
                            visualData[i] = wrappedVisualData.getInt();
                        }

                        ThermalImage nextImage = new ThermalImage(width, height, thermalData, visualData);

//                    ThermalImage nextImage = (ThermalImage) inputStream.readObject();
//                    System.out.println("Received image");
                        BufferedImage bufferedImage = toImage(nextImage);
                        previewFrame.updatePreview(bufferedImage);
//                this.send(nextImage);
                    }

                } catch (EOFException e) {
                    System.out.println("Connection closed by client");
                    hasConnection = false;
                    this.stop();
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
