package de.dhbw.research.human.fade.out.remote.client;

import de.dhbw.research.human.fade.out.remote.thermalImage.TemperatureRange;
import de.dhbw.research.human.fade.out.remote.thermalImage.ThermalImageJava;

import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class Client {

    private String ip;
    private int port;

    private Socket clientSocket;
    private DataOutputStream outputStream;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void startConnection() {
        try {
            clientSocket = new Socket(ip, port);

            outputStream = new DataOutputStream(clientSocket.getOutputStream());

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

    public void send(ThermalImageJava image) {
        try {
            image.send(outputStream);
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

        final TemperatureRange range = new TemperatureRange(30065, 30085);
        int[] thermalData = new int[307200];
        Arrays.fill(thermalData, 1);
        for (int i = 0; i < 307200 / 2; i++) {
            thermalData[i] = 30085;
        }

        BufferedImage image = createDummyImage(0xff0000);

        System.out.println("Send Image 1");

        client.send(new ThermalImageJava(image, thermalData, range, ThermalImageJava.MODE_NONE));
        System.out.println("Send Image 2");
        client.send(new ThermalImageJava(image, thermalData, range, ThermalImageJava.MODE_NONE));
        System.out.println("Send Image 3");
        client.send(new ThermalImageJava(image, thermalData, range, ThermalImageJava.MODE_NONE));
        System.out.println("Send Image 4");
        client.send(new ThermalImageJava(image, thermalData, range, ThermalImageJava.MODE_NONE));
        System.out.println("Send Image 5");
        client.send(new ThermalImageJava(image, thermalData, range, ThermalImageJava.MODE_NONE));

        image = createDummyImage(0x00ff00);

        System.out.println("Send Image 6");
        client.send(new ThermalImageJava(image, thermalData, range, ThermalImageJava.MODE_NONE));
        System.out.println("Send Image 7");
        client.send(new ThermalImageJava(image, thermalData, range, ThermalImageJava.MODE_NONE));
        System.out.println("Send Image 8");
        client.send(new ThermalImageJava(image, thermalData, range, ThermalImageJava.MODE_NONE));
        System.out.println("Send Image 9");
        client.send(new ThermalImageJava(image, thermalData, range, ThermalImageJava.MODE_NONE));
        System.out.println("Send Image 10");
        client.send(new ThermalImageJava(image, thermalData, range, ThermalImageJava.MODE_NONE));

        client.stopConnection();
    }
}
