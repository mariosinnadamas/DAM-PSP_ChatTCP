package org.mario.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * Clase hilo del servidor que gestiona las peticiones del cliente
 */
public class ClienteHandler extends Thread{

    private Socket socket;
    private List <ClienteHandler> clientes;
    private DataInputStream entrada;
    private DataOutputStream salida;

    public ClienteHandler(Socket socket, List<ClienteHandler> clientes) throws IOException {
        this.socket = socket;
        this.clientes = clientes;
        this.entrada = new DataInputStream(socket.getInputStream());
        this.salida = new DataOutputStream((socket.getOutputStream()));
    }

    @Override
    public void run() {
        try{
            System.out.println("Hilo Handler iniciado");
            while (true){
                String mensaje = entrada.readUTF();
                synchronized (clientes){
                    System.out.println(mensaje);
                    for (ClienteHandler c : clientes){
                        if (c != this){
                            c.salida.writeUTF("Mensaje recibido: " + mensaje);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Cliente desconectado: " + socket.getInetAddress());
        } finally {
            synchronized (clientes){ //Si un cliente se desconecta o pasa cualquier cosa lo elimina de la lista
                clientes.remove(this);
            }
            try {
                socket.close();
            } catch (IOException e) {}
        }
    }
}
