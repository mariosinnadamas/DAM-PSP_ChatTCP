package org.mario.server;

/**
 * Clase servidor que lo que hace es:
 * - Montar servidor
 * - Aceptar clientes
 * - Agregar a una lista común con todos los clientes agregados
 * - Arrancar la clase que maneja los clientes
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    //Map de clientes identificados por nickname
    public static Map<String, ClienteHandler> clientes = new ConcurrentHashMap<>();

    //Cada canal tiene una lista de clientes conectados a ese canal
    public static Map<String, List<ClienteHandler>> canales = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int nPuerto = 6000;
        try (ServerSocket server = new ServerSocket(nPuerto)){
            System.out.println("Servidor iniciado...");
                while (true){
                    Socket socket = server.accept(); //Acepta al cliente
                    System.out.println("Cliente aceptado: "+ socket.getInetAddress());

                    ClienteHandler ch = new ClienteHandler(socket, clientes); //Le pasa al gestor de clientes la lista con los clientes
                    ch.start(); //Inicia el gestor
                }
        } catch (IOException e) {
            System.err.println("ERROR: No se pudo iniciar el servidor en el puerto " + nPuerto);
            System.err.println("MOTIVO: " + e.getMessage());
        }
    }
}