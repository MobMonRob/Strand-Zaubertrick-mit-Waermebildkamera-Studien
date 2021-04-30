package de.dhbw.research.human.fade.out.remote.server;

import de.dhbw.research.human.fade.out.remote.imageProcessor.ImageProcessor;
import de.dhbw.research.human.fade.out.remote.thermalImage.ThermalImageJava;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;

public class Server {

    private static final boolean MEASURE_FPS = true;
    private static final boolean MEASURE_PROCESSING_TIME = true;

    private int port;

    private ImageProcessor imageProcessor;
    private ImageProcessor secondaryImageProcessor;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream inputStream;

    private Instant lastReceived = Instant.now();

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

                        if (MEASURE_FPS) {
                            Instant currentReceived = Instant.now();
                            long elapsedMilliseconds = Duration.between(lastReceived, currentReceived).toMillis();
                            System.out.println("Time - Receive: " + (1000F / elapsedMilliseconds) + " FPS");
                            lastReceived = currentReceived;
                        }

                        if (MEASURE_PROCESSING_TIME) {
                            Instant start = Instant.now();
                            imageProcessor.onImageReceived(thermalImage);
                            Instant end = Instant.now();
                            System.out.println("Time - Processing: " + Duration.between(start, end).toMillis() + " ms");
                        } else {
                            imageProcessor.onImageReceived(thermalImage);
                        }

                        if (secondaryImageProcessor != null) {
                            secondaryImageProcessor.onImageReceived(thermalImage);
                        }
                    } catch (EOFException e) {
                        imageProcessor.onConnectionClosed();
                        if (secondaryImageProcessor != null) {
                            secondaryImageProcessor.onConnectionClosed();
                        }

                        System.out.println("Connection closed by client");
                        e.printStackTrace();
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
}
