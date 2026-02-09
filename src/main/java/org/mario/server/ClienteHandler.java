package org.mario.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Clase hilo del servidor que gestiona las peticiones del cliente
 */
public class ClienteHandler extends Thread{

    String nickName;
    String destinatario;
    private Socket socket;
    private Map<String, ClienteHandler> clientes;
    private DataInputStream entrada;
    private DataOutputStream salida;

    public ClienteHandler(Socket socket, Map<String, ClienteHandler> clientes) throws IOException {
        this.socket = socket;
        this.clientes = clientes;
        this.entrada = new DataInputStream(socket.getInputStream());
        this.salida = new DataOutputStream((socket.getOutputStream()));
    }

    /**
     * Lectura del mensaje en destino al chat privado
     *
     * @param mensaje la entrada del argumento, esperando '@' como primer caracter seguido del nickname y del contenido
     * @return Un Array de Strings donde el primer elemento es el nickname y el segundo elemento el contenido.
     */
    public String[] leerNickName(String mensaje){
        mensaje = mensaje.trim();
        int espacio = mensaje.indexOf(" ");

        String nick;
        String contenido;

        if (espacio == -1) {
            nick = mensaje.substring(1);
            contenido = "";
        } else {
            nick = mensaje.substring(1, espacio);
            contenido = mensaje.substring(espacio + 1).trim();
        }
        return new String[] {nick, contenido};
    }

    /**
     * Si el nick es único, se almacena y confirma. De lo contrario, se solicita al cliente que escriba otro nick.
     *
     * @param entrada DataInputStream El cliente envía el nick como una cadena UTF que se convertirá a minúsculas.
     * @param salida DataOutputStream Envía "CORRECTO" si el apodo es único o "REPETIDO" si ya está en uso.
     *
     * @throws IOException if an I/O error occurs while reading or writing data to the client.
     */
    public void validarNickName(DataInputStream entrada, DataOutputStream salida) throws IOException {
        while (true) {
            nickName = entrada.readUTF().toLowerCase();

            // si no existe devuelve null
            if (clientes.putIfAbsent(nickName, this) == null) {
                salida.writeUTF("CORRECTO");
                salida.flush();
                break;
            } else {
                salida.writeUTF("REPETIDO");
                salida.flush();
            }
        }
    }

    @Override
    public void run() {
        try{
            System.out.println("Hilo Handler iniciado");

            validarNickName(entrada, salida);

            while (true){
                String mensaje = entrada.readUTF().trim();
                // identificacion si es privado o no
                if (mensaje.startsWith("@")){
                    String[] partes = leerNickName(mensaje);
                    destinatario = partes[0];
                    String contenido = partes[1];

                    System.out.println(mensaje);

                    ClienteHandler destino = clientes.get(destinatario);
                    if (destino != null){
                        destino.salida.writeUTF( nickName + ": " + contenido);
                    }

                } else {
                    System.out.println(mensaje);

                    for (ClienteHandler c : clientes.values()){
                        if (c != this){
                            c.salida.writeUTF("todos: " + mensaje);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Cliente desconectado: " + socket.getInetAddress());
        } finally {
            synchronized (clientes){ //Si un cliente se desconecta o pasa cualquier cosa lo elimina de la lista
                if (nickName != null) {
                    clientes.remove(nickName);
                }

            }
            try {
                socket.close();
            } catch (IOException e) {}
        }
    }
}