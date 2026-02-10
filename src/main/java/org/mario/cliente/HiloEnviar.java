package org.mario.cliente;

/**
 * Esta clase se encarga de enviar los mensajes al servidor.
 */

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Hilo encargado de enviar mensajes al servidor.
 * Lee mensajes desde consola y los envía a través del socket.
 * Si el usuario escribe '*', se desconecta del servidor.
 */
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
           System.out.println("Introduce un mensaje (* para salir)");

           while (true){
               String mensaje = sc.nextLine();

               //Envia mensaje al servidor
               salida.writeUTF(mensaje);

               //Si el usuario quiere salir
               if (mensaje.equalsIgnoreCase("*")){
                   System.out.println("Desconectado del servidor...");
                   socket.close();
                   break;
               }
           }
       } catch (IOException e) {
           System.err.println("ERROR: No se pudo enviar el mensaje. Posible desconexión del servidor.");
       } finally {
           // Cerrar recursos
           try {
               if (salida != null) salida.close();
           } catch (IOException ignored) {}
           try { if (socket != null && !socket.isClosed()) socket.close();
           } catch (IOException ignored) {}

           sc.close();
       }
    }
}
