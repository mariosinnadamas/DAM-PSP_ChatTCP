package org.mario.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) {
        int nPuerto = 6000;
        List<ClienteHandler> clientes = new ArrayList<>();
        while (true){
            try (ServerSocket server = new ServerSocket(nPuerto)){
                System.out.println("Servidor iniciado...");
                while (true){
                    Socket socket = server.accept();
                    System.out.println("Cliente aceptado: " + socket.getInetAddress());

                    ClienteHandler ch = new ClienteHandler(socket,clientes);
                    clientes.add(ch);
                    ch.start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
