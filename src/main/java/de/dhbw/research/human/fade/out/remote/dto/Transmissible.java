package de.dhbw.research.human.fade.out.remote.dto;

import java.io.BufferedOutputStream;
import java.io.OutputStream;

public interface Transmissible {

    void send(BufferedOutputStream outputStream);
}
