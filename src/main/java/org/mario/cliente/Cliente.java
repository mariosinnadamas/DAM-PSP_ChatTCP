package org.mario.cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Clase cliente que se le asocia un nickname, valida su disponibilidad con el servidor
 * y luego inicia los hilos para enviar y recibir mensajes.
 */
public class Cliente {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Socket socket = null;
        DataInputStream entrada = null;
        DataOutputStream salida = null;

        try {
            socket = new Socket("localhost", 6000);
            entrada = new DataInputStream(socket.getInputStream());
            salida = new DataOutputStream(socket.getOutputStream());

            String nombre;

            //Validación del nickname
            while (true) {
                System.out.print("Introduce tu nombre de usuario: ");
                nombre = sc.nextLine();


                if (nombre == null || nombre.isBlank()){
                    System.out.println("El nombre no puede estar vacío");
                    continue;
                }

                salida.writeUTF(nombre);
                String respuesta = entrada.readUTF();

                if (respuesta.equals("CORRECTO")) {
                    break;
                } else if (respuesta.equals("REPETIDO")) {
                    System.out.println("Ese nickName ya está en uso");
                }
            }

            System.out.println("Cliente iniciado y conectado a " + socket.getPort());

            //Iniciar hilos de envío y recepción
            HiloEnviar envio = new HiloEnviar(salida,socket);
            HiloRecibir recibir = new HiloRecibir(entrada);

            envio.start();
            recibir.start();

        } catch (UnknownHostException e) {
            System.err.println("ERROR: No se pudo conectar con el servidor. Revisa la dirección.");
        } catch (IOException e) {
            System.err.println("ERROR: No se pudo establecer la conexión con el servidor.");
            System.err.println("MOTIVO: " + e.getMessage());
        } finally {
            //Cierre de recursos
            // (ignored para indicar que se sabe que ahí puede suceder una excepción pero que no necesita ser tratada)
            if (socket != null && socket.isClosed()) {
                try {
                    if (entrada != null) entrada.close();
                } catch (IOException ignored) {}
                try {
                    if (salida != null) salida.close();
                } catch (IOException ignored) {}
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }
    }
}
