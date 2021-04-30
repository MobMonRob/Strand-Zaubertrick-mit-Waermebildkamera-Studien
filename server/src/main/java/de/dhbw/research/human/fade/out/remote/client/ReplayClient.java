package de.dhbw.research.human.fade.out.remote.client;

import de.dhbw.research.human.fade.out.remote.thermalImage.ThermalImageJava;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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

    public void send(ThermalImageJava image) {
        try {
            image.send(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ReplayClient client = new ReplayClient("127.0.0.1", 4444);
//        ReplayClient client = new ReplayClient("192.168.170.55", 4444);
//        ReplayClient client = new ReplayClient("localhost", 4444);
        client.startConnection();

        BufferedImage background1 = ImageIO.read(new File("./test-background1.jpg"));
        BufferedImage background2 = ImageIO.read(new File("./test-background2.jpg"));
        BufferedImage background3 = ImageIO.read(new File("./test-background1.jpg"));
        BufferedImage background4 = ImageIO.read(new File("./test-background2.jpg"));
        BufferedImage background5 = ImageIO.read(new File("./test-background1.jpg"));

        final DataInputStream inputStream = new DataInputStream(new FileInputStream("test-render-time-18:04:2021-14:01:16"));
//        final DataInputStream inputStream = new DataInputStream(new FileInputStream("quality-test-mask-21:04:2021-22:58:02"));
//        final DataInputStream inputStream = new DataInputStream(new FileInputStream("opencv-test-21:04:2021-23:03:18"));

        int sendCount = 0;
        while (true) {
            try {
                ThermalImageJava thermalImage = ThermalImageJava.receive(inputStream);
//                BufferedImage image = new BufferedImage(thermalImage.getWidth(), thermalImage.getHeight(), thermalImage.getBufferedImage().getType());
//                int[] maskData = Arrays.stream(thermalImage.getBooleanThermalMask()).mapToInt(value -> value ? 0xff000000 : 0).toArray();
//                BufferedImage mask = new BufferedImage(thermalImage.getWidth(), thermalImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
//                mask.setRGB(0, 0, thermalImage.getWidth(), thermalImage.getHeight(), maskData, 0, thermalImage.getWidth());

//                client.send(new ThermalImageJava(background2, thermalImage.getThermalMask(), ThermalImage.MODE_PHOTO));
//                if (sendCount >= 0 && sendCount <= 20) {
//                    image.getGraphics().drawImage(background1, 0, 0, null);
//                } else if (sendCount >= 21 && sendCount <= 51) {
//                    image.getGraphics().drawImage(background2, 0, 0, null);
//                } else if (sendCount >= 52 && sendCount < 82) {
//                    image.getGraphics().drawImage(background3, 0, 0, null);
//                } else if (sendCount >= 82 && sendCount <= 107) {
//                    image.getGraphics().drawImage(background4, 0, 0, null);
//                } else {
//                    image.getGraphics().drawImage(background5, 0, 0, null);
//                }
//                image.getGraphics().drawImage(mask, 0, 0, null);
//                client.send(new ThermalImageJava(image, thermalImage.getThermalMask(), ThermalImage.MODE_PHOTO));
                client.send(thermalImage);
                sendCount++;

                System.out.println("Send image " + sendCount);
                Thread.sleep(100);
            } catch (IOException | InterruptedException e) {
                break;
            }
        }
        client.stopConnection();
    }
}
