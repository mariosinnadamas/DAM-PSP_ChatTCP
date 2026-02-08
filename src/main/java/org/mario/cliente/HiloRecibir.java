package org.mario.cliente;
/**
 * Esta clase se encarga de recibir los mensajes e imprimirlos por pantalla.
 */

import java.io.DataInputStream;
import java.io.IOException;

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
            System.err.println("Conexión cerrada");
        }
    }
}
