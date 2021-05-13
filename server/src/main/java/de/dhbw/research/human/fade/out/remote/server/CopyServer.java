package de.dhbw.research.human.fade.out.remote.server;

import de.dhbw.research.human.fade.out.remote.imageProcessor.CaptureImageProcessor;
import de.dhbw.research.human.fade.out.remote.imageProcessor.CopyImageProcessor;
import nu.pattern.OpenCV;
import org.opencv.core.Core;

import java.io.FileNotFoundException;

public class CopyServer {

    public static void main(String[] args) throws FileNotFoundException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadShared();

        if (args.length == 2 && args[0].equals("record")) {
            new Server(4444, new CopyImageProcessor(), new CaptureImageProcessor(args[1])).start();
        } else {
            new Server(4444, new CopyImageProcessor()).start();
        }
    }
}
