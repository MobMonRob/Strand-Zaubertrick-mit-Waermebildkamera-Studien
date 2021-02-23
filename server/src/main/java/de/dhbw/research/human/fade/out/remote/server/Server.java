package de.dhbw.research.human.fade.out.remote.server;

import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;
import de.dhbw.research.human.fade.out.remote.imageProcessor.CaptureImageProcessor;
import de.dhbw.research.human.fade.out.remote.imageProcessor.ImageProcessor;
import de.dhbw.research.human.fade.out.remote.imageProcessor.OpenCVImageProcessor;
import nu.pattern.OpenCV;
import org.opencv.core.Core;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private int port;

    private ImageProcessor imageProcessor;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream inputStream;

    public Server(int port, ImageProcessor imageProcessor) {
        this.port = port;
        this.imageProcessor = imageProcessor;
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
                        e.printStackTrace();
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

    public static void main(String[] args) throws FileNotFoundException {
        ImageProcessor imageProcessor;
        if (args.length == 2 && args[0].equals("record")) {
            imageProcessor = new CaptureImageProcessor(args[1]);
        } else {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            OpenCV.loadShared();
            imageProcessor = new OpenCVImageProcessor();
        }
        Server server = new Server(4444, imageProcessor);
        server.start();
    }
}
