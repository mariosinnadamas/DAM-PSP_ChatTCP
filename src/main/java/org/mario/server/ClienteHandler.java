package org.mario.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.mario.server.Server.canales;

/**
 * Hilo del servidor encargado de gestionar la comunicación con un cliente concreto.
 * Cada cliente conectado al servidor se maneja mediante una instancia de esta clase,
 * lo que permite que varios clientes funcionen de forma concurrente
 */
public class ClienteHandler extends Thread{

    private String nickName; //Nombre del usuario (clave en el HashMap)
    private String destinatario;
    private Socket socket;
    private String canalActual = "general";
    private Map<String, ClienteHandler> clientes; //Mapa de clientes conectados
    private DataInputStream entrada;
    private DataOutputStream salida;

    /**
     * Constructor del manejador de clientes.
     * El constructor recibe por parámetro el Socket (cliente) y el Map con clientes.
     * @param socket asociado al cliente
     * @param clientes mapa de clientes conectados
     * @throws IOException
     */

    public ClienteHandler(Socket socket, Map<String, ClienteHandler> clientes) throws IOException {
        this.socket = socket;
        this.clientes = clientes;
        this.entrada = new DataInputStream(socket.getInputStream());
        this.salida = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Lectura del mensaje en destino al chat privado. Formato "@usuario contenido"
     *
     * @param mensaje Mensaje recibido del cliente
     * @return Array con dos elementos: [Nickname, contenido]
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
     * @throws IOException si un error de I/o ocurre mientras se está escribiendo leyendo datos del cliente.
     */
    public void validarNickName(DataInputStream entrada, DataOutputStream salida) throws IOException {
        while (true) {
            nickName = entrada.readUTF().toLowerCase();

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

    /**
     * Metodo para cambiar del canal general a otro canal.
     * Lo elimina de su canal anterior y lo añade al nuevo.
     * @param nuevoCanal El nombre del nuevo canal al que se quiere mover el usuario.
     */

    public void cambiarCanal(String nuevoCanal){
        // Salir del canal actual
        if (canalActual != null && canales.containsKey(canalActual)) {
            canales.get(canalActual).remove(this);
        }

        // PutIfAbsent sirve para añadir un par clave-valor solo si la clave no existe o si está asociada a un valor null
        // Copy es una colección segura para hilos, optimizada para entornos concurrentes que se va a leer mucho más de lo que se va a modificar

        canales.putIfAbsent(nuevoCanal, new CopyOnWriteArrayList<>());
        canales.get(nuevoCanal).add(this);

        canalActual = nuevoCanal;

        System.out.println(nickName + " se ha unido al canal " + nuevoCanal);
    }

    /**
     * Procesa comandos especiales enviados por el cliente.
     * - //salir → vuelve al canal general
     * - //nombreCanal → cambia al canal indicado
     */

    public void procesarComando(String comando) throws IOException{

        if (comando.equalsIgnoreCase("//salir")){
            cambiarCanal("general");
            salida.writeUTF("Has vuelto al chat general");
            return;
        }

        if (comando.startsWith("//") && comando.length() > 2){
            String canal = comando.substring(2).trim();
            cambiarCanal(canal);
            salida.writeUTF("Te has unido al canal " + canalActual);
        }
    }

    /**
     * Metodo principal del hilo.
     * Gestiona:
     * - Validación del nickname
     * - Recepción de mensajes
     * - Envío de mensajes públicos y privados
     * - Cambio de canales
     */

    @Override
    public void run() {
        try{
            validarNickName(entrada, salida);
            cambiarCanal("general");

            while (true){
                String mensaje = entrada.readUTF().trim();

                // Mensaje privado
                if (mensaje.startsWith("@")){
                    String[] partes = leerNickName(mensaje);
                    destinatario = partes[0];
                    String contenido = partes[1];

                    System.out.println("Mensaje privado: " + mensaje);

                    ClienteHandler destino = clientes.get(destinatario);

                    if (destino != null && destino.canalActual.equals(this.canalActual)){
                        destino.salida.writeUTF( "(privado) | " + nickName + ": " + contenido);
                    } else {
                        salida.writeUTF("El usuario no está en tu canal");
                    }
                    //Comando
                } else if (mensaje.startsWith("//")) {
                    procesarComando(mensaje);

                    //Mensaje público
                } else {
                    System.out.println(mensaje);
                    for (ClienteHandler c : canales.get(canalActual)){
                        if (c != this){
                            c.salida.writeUTF(canalActual + " | " + nickName + ": " + mensaje);
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("ERROR: Cliente desconectado: " + nickName + " (" + socket.getInetAddress() + ")");
        } finally {
            //Elimino del mapa global
            synchronized (clientes){
                if (nickName != null) {
                    clientes.remove(nickName);
                }
            }

            //Elimino del canal actual
            if (canalActual != null && canales.containsKey(canalActual)){
                canales.get(canalActual).remove(this);
            }

            //Cerrar streams
            try {
                if (salida != null) salida.close();
            } catch (IOException e) {}

            try {
                socket.close();
            } catch (IOException e) {}
        }
    }
}