package org.mario.server;

import java.net.Socket;
import java.util.List;

/**
 * Clase hilo del servidor que gestiona las peticiones del cliente
 */
public class ClienteHandler extends Thread{

    private Socket socket;
    private List <ClienteHandler> clientes;

    public ClienteHandler(Socket socket, List<ClienteHandler> clientes) {
        this.socket = socket;
        this.clientes = clientes;
    }

    @Override
    public void run() {
        
    }
}
