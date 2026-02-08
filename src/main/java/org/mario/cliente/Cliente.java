package org.mario.cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost",6000);
            DataInputStream entrada = new DataInputStream(socket.getInputStream());
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());

            Scanner sc = new Scanner(System.in);
            System.out.print("Introduce tu nombre de usuario: ");
            String nombre = sc.nextLine();
            salida.writeUTF(nombre);

            System.out.println("Cliente iniciado y conectado a " + socket.getPort());

            //El cliente tiene 2 hilos, uno para enviar mensajes y otro para leerlos
            HiloEnviar envio = new HiloEnviar(salida,socket);
            HiloRecibir recibir = new HiloRecibir(entrada);
            envio.start();
            recibir.start();

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
