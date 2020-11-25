package de.dhbw.research.human.fade.out.remote;

import android.os.AsyncTask;
import de.dhbw.research.human.fade.out.remote.dto.ThermalImage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AsyncClient extends AsyncTask<Void, Void, Void> {

    private String ip;
    private int port;

    private Socket clientSocket;
    private ObjectOutputStream outputStream;
    private Queue<ThermalImage> thermalImages = new ConcurrentLinkedQueue<>();
    private boolean active = false;

    public AsyncClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    protected Void doInBackground(Void... voids) {
        try {
            clientSocket = new Socket(ip, port);

            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error while starting server:");
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
                                outputStream.writeObject(image);
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
//            inputStream.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error while closing connection:");
            e.printStackTrace();
        }
    }
}
