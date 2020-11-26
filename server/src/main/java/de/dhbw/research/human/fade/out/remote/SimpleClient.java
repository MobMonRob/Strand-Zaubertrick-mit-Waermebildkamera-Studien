package de.dhbw.research.human.fade.out.remote;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SimpleClient extends AsyncTask<Void, Void, Void> {

    protected Void doInBackground(Void... voids) {
        Socket socket = null;
        try {
            socket = new Socket("192.168.43.149", 4444);
            InputStream input = socket.getInputStream();
            long total = 0;
            long start = System.currentTimeMillis();

            byte[] bytes = new byte[10240]; // 10K
            while (true) {
                int read = input.read(bytes);
                total += read;
                long cost = System.currentTimeMillis() - start;
                if (cost > 0 && System.currentTimeMillis() % 10 == 0) {
                    Log.i("Socket", "Read " + total + " bytes, speed: " + total / cost + "KB/s");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("127.0.0.1", 4444);
        InputStream input = socket.getInputStream();
        long total = 0;
        long start = System.currentTimeMillis();

        byte[] bytes = new byte[10240]; // 10K
        while (true) {
            int read = input.read(bytes);
            total += read;
            long cost = System.currentTimeMillis() - start;
            if (cost > 0 && System.currentTimeMillis() % 10 == 0) {
                System.out.println("Read " + total + " bytes, speed: " + total / cost + "KB/s");
            }
        }
    }
}