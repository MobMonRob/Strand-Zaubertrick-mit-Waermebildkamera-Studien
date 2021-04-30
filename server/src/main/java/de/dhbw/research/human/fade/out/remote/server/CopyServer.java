package de.dhbw.research.human.fade.out.remote.server;

import de.dhbw.research.human.fade.out.remote.imageProcessor.CaptureImageProcessor;
import de.dhbw.research.human.fade.out.remote.imageProcessor.CopyImageProcessor;

import java.io.FileNotFoundException;

public class CopyServer {

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 2 && args[0].equals("record")) {
            new Server(4444, new CopyImageProcessor(), new CaptureImageProcessor(args[1])).start();
        } else {
            new Server(4444, new CopyImageProcessor()).start();
        }
    }
}
