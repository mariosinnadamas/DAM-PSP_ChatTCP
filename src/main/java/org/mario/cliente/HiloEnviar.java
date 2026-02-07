package org.mario.cliente;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class HiloEnviar extends Thread{

    private DataOutputStream salida;
    Scanner sc;

    public HiloEnviar(DataOutputStream salida) {
        this.salida = salida;
        this.sc = new Scanner(System.in);
    }

    @Override
    public void run() {
       try {
           String mensaje = "";
           while (!mensaje.equalsIgnoreCase("*")){
               System.out.println("Introduce un mensaje (* para salir)");
               mensaje = sc.nextLine();
               salida.writeUTF(mensaje);
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
    }
}
