# Estructura
La estructura a seguir es esta:
- Clase servidor
- Clase GestorClientes (HiloCliente) --> Esta clase gestiona los hilos de clientes
- Clase Cliente (Se conecta al servidor)
- Clase HiloEnviarMensajes (Hilo que se encarga de enviar mensajes)
- Clase HiloRecibirMensajes (Hilo que se encarga de recibir mensajes)

Con esta estructura nos garantizamos que no haya bloqueos de mensajes ni de hilos en ningún momento.
Primero estoy haciendo el diseño de conexión Cliente-Servidor sin hilos.
Ahora estoy intentando que el cliente envie y reciba mensajes a través de hilos.
