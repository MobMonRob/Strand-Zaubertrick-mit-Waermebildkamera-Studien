package de.dhbw.research.human.fade.out.remote.client;

import android.os.AsyncTask;
import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AndroidClient extends AsyncTask<Void, Void, Void> {

    private final String ip;
    private final int port;
    private int maskTemperature;

    private Socket clientSocket;
    private DataOutputStream outputStream;
    private final Queue<ThermalImage> thermalImages = new ConcurrentLinkedQueue<ThermalImage>();
    private boolean active = false;

    public AndroidClient(String ip, int port, int maskTemperature) {
        this(ip, port);
        this.maskTemperature = maskTemperature;
    }

    public AndroidClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    protected Void doInBackground(Void... voids) {
        try {
            clientSocket = new Socket(ip, port);

            outputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error while starting client:");
            e.printStackTrace();
        }
        return null;
    }

    public void sendImage(ThermalImage image, boolean clearQueue) {
        if (clearQueue) {
            thermalImages.clear();
        }
        thermalImages.add(image);
    }

    public void startConnection() {
        this.execute();
        active = true;
        new Thread(new Runnable() {
            public void run() {
                while (active) {
                    if (outputStream != null) {
                        ThermalImage image = thermalImages.poll();
                        if (image != null) {
                            try {
                                image.send(outputStream, maskTemperature);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    public void closeConnection() {
        active = false;
        try {
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error while closing connection:");
            e.printStackTrace();
        }
    }

    public void setMaskTemperature(int maskTemperature) {
        this.maskTemperature = maskTemperature;
    }
}
