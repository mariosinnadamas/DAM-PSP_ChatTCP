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
        try (ServerSocket server = new ServerSocket(nPuerto)){
            System.out.println("Servidor iniciado...");

            Socket cliente = server.accept();
            System.out.println("Cliente conectado desde " + cliente.getInetAddress());

            DataInputStream flujoEntrada = new DataInputStream(cliente.getInputStream());
            DataOutputStream flujoSalida = new DataOutputStream(cliente.getOutputStream());

            String mensaje;

            while (!(mensaje = flujoEntrada.readUTF()).equalsIgnoreCase("*")){
                System.out.println("Mensaje del cliente: " + mensaje);
                flujoSalida.writeUTF("ECO DEL SERVIDOR " + mensaje);
            }

            System.out.println("Cerrando servidor...");

            flujoEntrada.close();
            flujoSalida.close();
            cliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
