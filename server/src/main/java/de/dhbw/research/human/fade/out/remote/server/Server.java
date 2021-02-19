package de.dhbw.research.human.fade.out.remote.server;

import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;
import de.dhbw.research.human.fade.out.remote.imageProcessor.ImageProcessor;
import de.dhbw.research.human.fade.out.remote.imageProcessor.OpenCVImageProcessor;
import nu.pattern.OpenCV;
import org.opencv.core.Core;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private int port;

    private ImageProcessor imageProcessor;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream inputStream;

    public Server(int port) {
        this.port = port;
        imageProcessor = new OpenCVImageProcessor();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {

                clientSocket = serverSocket.accept();

                inputStream = new DataInputStream(clientSocket.getInputStream());

                boolean hasConnection = true;
                while (hasConnection) {
                    try {
                        final ThermalImage thermalImage = ThermalImage.receive(inputStream);
                        imageProcessor.onImageReceived(thermalImage);
                    } catch (EOFException e) {
                        System.out.println("Connection closed by client");
                        hasConnection = false;
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

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadShared();
        Server server = new Server(4444);
        server.start();
    }
}
