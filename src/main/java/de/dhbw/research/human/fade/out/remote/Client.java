package de.dhbw.research.human.fade.out.remote;

import android.graphics.Bitmap;
import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    private String ip;
    private int port;

    private ImageReceivedHandler imageReceivedHandler;

    private boolean keepRunning = true;
    private Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public Client(String ip, int port, ImageReceivedHandler imageReceivedHandler) {
        this(ip, port);
        this.imageReceivedHandler = imageReceivedHandler;
    }

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void setImageReceivedHandler(ImageReceivedHandler imageReceivedHandler) {
        this.imageReceivedHandler = imageReceivedHandler;
    }

    public void startConnection() {
        try {
            clientSocket = new Socket(ip, port);

            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            inputStream = new ObjectInputStream(clientSocket.getInputStream());

            new Thread(new Runnable() {
                public void run() {
                    while (keepRunning) {
                        try {
                            Bitmap image = (Bitmap) inputStream.readObject();
                            System.out.println("Received image");
                            imageReceivedHandler.onImageReceived(image);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        } catch (IOException e) {
            System.out.println("Error while starting server:");
            e.printStackTrace();
        }
    }

    public void stopConnection() {
        keepRunning = false;
        try {
            inputStream.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error while closing connection:");
            e.printStackTrace();
        }
    }

    public void send(ThermalImage image) {
        try {
            outputStream.writeObject(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface ImageReceivedHandler {
        void onImageReceived(Bitmap image);
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 4444);
        client.startConnection();
        client.send(new ThermalImage(1, 1, new int[]{0}, new int[]{0xff0000}));
        client.stopConnection();
    }
}
