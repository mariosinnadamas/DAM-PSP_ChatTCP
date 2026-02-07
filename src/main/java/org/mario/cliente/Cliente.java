package org.mario.cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost",6000);
             DataInputStream entrada = new DataInputStream(socket.getInputStream());
             DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
             Scanner sc = new Scanner(System.in)){

            System.out.println("Cliente iniciado y conectado a " + socket.getPort());

            HiloEnviar envio = new HiloEnviar(salida);
            HiloRecibir recibir = new HiloRecibir(entrada);
            envio.start();
            recibir.start();

            envio.join(); //Para hacer esperar al main
            recibir.join();

            System.out.println("Desconectando...");

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
