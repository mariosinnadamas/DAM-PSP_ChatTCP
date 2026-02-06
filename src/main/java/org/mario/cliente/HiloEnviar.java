package org.mario.cliente;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class HiloEnviar extends Thread{

    private DataOutputStream salida;


    @Override
    public void run() {
        while (true){
            Scanner sc = new Scanner(System.in);
            String mensaje = "";

            try {
                while (!mensaje.equalsIgnoreCase("*")){
                    System.out.println("Introduce un mensaje (* para salir): ");
                    mensaje = sc.nextLine();
                    salida.writeUTF(mensaje);
                    if (mensaje.equalsIgnoreCase("*")){
                        break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
