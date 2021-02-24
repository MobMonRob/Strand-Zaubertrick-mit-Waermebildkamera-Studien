package de.dhbw.research.human.fade.out.remote.client;

import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReplayClient {

    private String ip;
    private int port;

    private Socket clientSocket;
    private DataOutputStream outputStream;

    public ReplayClient(String ip, int port) {
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

    public void send(ThermalImage image) {
        try {
            image.send(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ReplayClient client = new ReplayClient("localhost", 4444);
        client.startConnection();

        final DataInputStream inputStream = new DataInputStream(new FileInputStream("recorded/cat-test-24:02:2021-08:46:54"));

        int sendCount = 0;
        while (true) {
            try {
                ThermalImage thermalImage = ThermalImage.receive(inputStream);
                client.send(thermalImage);
                sendCount++;
                System.out.println("Send image " + sendCount);
                Thread.sleep(400);
            } catch (IOException | InterruptedException e) {
                break;
            }
        }
        client.stopConnection();
    }
}
