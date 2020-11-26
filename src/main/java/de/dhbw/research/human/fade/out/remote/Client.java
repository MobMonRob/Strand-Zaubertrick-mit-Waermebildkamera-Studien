package de.dhbw.research.human.fade.out.remote;

import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

public class Client {

    private String ip;
    private int port;

    private Socket clientSocket;
    private ObjectOutputStream outputStream;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void startConnection() {
        try {
            clientSocket = new Socket(ip, port);

            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

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
        try {
            outputStream.writeUnshared(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage createDummyImage(int color) {
        BufferedImage bufferedImage = new BufferedImage(480 , 640, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < 480; x++) {
            for (int y = 0; y < 640; y++) {
                bufferedImage.setRGB(x, y, color);
            }
        }
        return bufferedImage;
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 4444);
        client.startConnection();

        int[] thermalData = new int[307200];
        Arrays.fill(thermalData, 1);

        BufferedImage image = createDummyImage(0xff0000);

        client.send(new ThermalImage(480, 640, thermalData, image));
        client.send(new ThermalImage(480, 640, thermalData, image));
        client.send(new ThermalImage(480, 640, thermalData, image));
        client.send(new ThermalImage(480, 640, thermalData, image));
        client.send(new ThermalImage(480, 640, thermalData, image));

        image = createDummyImage(0x00ff00);

        client.send(new ThermalImage(480, 640, thermalData, image));
        client.send(new ThermalImage(480, 640, thermalData, image));
        client.send(new ThermalImage(480, 640, thermalData, image));
        client.send(new ThermalImage(480, 640, thermalData, image));
        client.send(new ThermalImage(480, 640, thermalData, image));

        client.stopConnection();
    }
}
