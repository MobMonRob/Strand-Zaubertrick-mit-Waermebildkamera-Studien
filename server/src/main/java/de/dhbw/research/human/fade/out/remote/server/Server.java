package de.dhbw.research.human.fade.out.remote.server;

import de.dhbw.research.human.fade.out.remote.imageProcessor.CaptureImageProcessor;
import de.dhbw.research.human.fade.out.remote.imageProcessor.ImageProcessor;
import de.dhbw.research.human.fade.out.remote.imageProcessor.OpenCVImageProcessor;
import de.dhbw.research.human.fade.out.remote.thermalImage.ThermalImageJava;
import nu.pattern.OpenCV;
import org.opencv.core.Core;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;

public class Server {

    private int port;
    private boolean measureFps = true;

    private ImageProcessor imageProcessor;
    private ImageProcessor secondaryImageProcessor;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream inputStream;

    private LocalDateTime lastReceived = LocalDateTime.now();

    public Server(int port, ImageProcessor imageProcessor, ImageProcessor secondaryImageProcessor) {
        this.port = port;
        this.imageProcessor = imageProcessor;
        this.secondaryImageProcessor = secondaryImageProcessor;
    }

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
                        final ThermalImageJava thermalImage = ThermalImageJava.receive(inputStream);
                        if (measureFps) {
                            LocalDateTime currentReceived = LocalDateTime.now();
                            System.out.println((1000F / Duration.between(lastReceived, currentReceived).toMillis()) + " FPS");
                            lastReceived = currentReceived;
                        }
                        imageProcessor.onImageReceived(thermalImage);
                        if (secondaryImageProcessor != null) {
                            secondaryImageProcessor.onImageReceived(thermalImage);
                        }
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
        Server server;
        ImageProcessor imageProcessor;

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadShared();
        imageProcessor = new OpenCVImageProcessor();

        if (args.length == 2 && args[0].equals("record")) {
            server = new Server(4444, imageProcessor, new CaptureImageProcessor(args[1]));
        } else {
            server = new Server(4444, imageProcessor);
        }
        server.start();
    }
}
