package org.mario.server;

/**
 * Clase servidor que lo único que hace es:
 * - Montar servidor
 * - Aceptar clientes
 * - Agregar a una lista común con todos los clientes agregados
 * - Arrancar la clase que maneja los clientes
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    public static Map<String, ClienteHandler> clientes = new ConcurrentHashMap<>();

    public static void altaCliente(String nickName, ClienteHandler handler){
        clientes.put(nickName, handler);
    }

    public static void main(String[] args) {
        int nPuerto = 6000;
        try (ServerSocket server = new ServerSocket(nPuerto)){
            System.out.println("Servidor iniciado…");
                while (true){
                    Socket socket = server.accept(); //Acepta al cliente
                    System.out.println("Cliente aceptado: "+ socket.getInetAddress());

                    ClienteHandler ch = new ClienteHandler(socket, clientes); //Le pasa al gestor de clientes la lista con los clientes CONECTADOS y el cliente nuevo
                    ch.start(); //Inicia el gestor
                }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}