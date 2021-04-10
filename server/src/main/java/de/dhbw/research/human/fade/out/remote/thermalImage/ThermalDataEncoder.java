package de.dhbw.research.human.fade.out.remote.thermalImage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ThermalDataEncoder {

    private static final int THREAD_COUNT = 4;

    public static byte[] encode(int[] thermalData, TemperatureRange range) {
        int size = thermalData.length;
        int chunkSize = thermalData.length / THREAD_COUNT;

        byte[] encodedBytes = new byte[size / 8];
        List<Thread> threads = IntStream.range(0, THREAD_COUNT)
                                        .mapToObj(i -> new Thread(() -> encodeBytes(chunkSize * i, chunkSize * (i + 1), thermalData, range, encodedBytes)))
                                        .collect(Collectors.toList());

        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return encodedBytes;
    }

    private static void encodeBytes(int start, int end, int[] thermalData, TemperatureRange range, byte[] dest) {
        int index = 0;
        byte encodedByte = 0;
        for (int i = start; i < end; i++) {
            index++;
            encodedByte <<= 1;
            encodedByte |= range.inRange(thermalData[i]) ? 1 : 0;
            if (index == 8) {
                dest[i / 8] = encodedByte;
                index = 0;
            }
        }
    }
}
