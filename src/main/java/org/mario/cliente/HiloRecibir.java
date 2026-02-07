package org.mario.cliente;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Scanner;

public class HiloRecibir extends Thread {

    private DataInputStream entrada;

    public HiloRecibir(DataInputStream entrada) {
        this.entrada = entrada;
    }

    @Override
    public void run() {
        try {
            while (true){
                String mensaje = entrada.readUTF();
                System.out.println(mensaje);
            }
        } catch (IOException e) {
            System.err.println("Servidor desconectado");
        }
    }
}
