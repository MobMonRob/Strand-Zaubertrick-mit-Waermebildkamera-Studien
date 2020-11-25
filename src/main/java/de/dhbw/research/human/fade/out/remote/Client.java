package de.dhbw.research.human.fade.out.remote;

import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class Client {

    private String ip;
    private int port;

    private Socket clientSocket;
    private BufferedOutputStream outputStream;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void startConnection() {
        try {
            clientSocket = new Socket(ip, port);

            outputStream = new BufferedOutputStream(clientSocket.getOutputStream());

        } catch (IOException e) {
            System.out.println("Error while starting server:");
            e.printStackTrace();
        }
    }

    public void stopConnection() {
        try {
            outputStream.flush();
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error while closing connection:");
            e.printStackTrace();
        }
    }

    public void send(ThermalImage image) {
        image.send(outputStream);
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 4444);
        client.startConnection();

        int[] thermalData = new int[307200];
        Arrays.fill(thermalData, 1);
        int[] visualData = new int[307200];
        Arrays.fill(visualData, 0xff0000);
        client.send(new ThermalImage(480, 640, thermalData, visualData));
        client.send(new ThermalImage(480, 640, thermalData, visualData));
        client.send(new ThermalImage(480, 640, thermalData, visualData));
        client.send(new ThermalImage(480, 640, thermalData, visualData));
        client.send(new ThermalImage(480, 640, thermalData, visualData));

        visualData = new int[307200];
        Arrays.fill(visualData, 0x00ff00);
        client.send(new ThermalImage(480, 640, thermalData, visualData));
        client.send(new ThermalImage(480, 640, thermalData, visualData));
        client.send(new ThermalImage(480, 640, thermalData, visualData));
        client.send(new ThermalImage(480, 640, thermalData, visualData));
        client.send(new ThermalImage(480, 640, thermalData, visualData));

        client.stopConnection();
    }
}
