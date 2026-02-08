package org.mario.cliente;

/**
 * Esta clase se encarga de enviar los mensajes al servidor.
 */

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class HiloEnviar extends Thread{

    private DataOutputStream salida;
    private Socket socket;
    Scanner sc;

    public HiloEnviar(DataOutputStream salida, Socket socket) {
        this.salida = salida;
        this.socket = socket;
        this.sc = new Scanner(System.in);
    }

    @Override
    public void run() {
       try {
           String mensaje;
           System.out.println("Introduce un mensaje (* para salir)");
           while (true){
               mensaje = sc.nextLine();
               salida.writeUTF(mensaje);
               if (mensaje.equalsIgnoreCase("*")){
                   socket.close();
                   break;
               }
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
    }
}
