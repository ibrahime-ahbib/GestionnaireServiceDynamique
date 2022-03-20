package bri;


import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;


public class ServeurBRi implements Runnable {
	//HashMap<Integer, Utilisateur> utilisateurs;
	private ServerSocket listen_socket;
	private Class<? extends Service> service;

	// Cree un serveur TCP - objet de la classe ServerSocket
	public ServeurBRi(int port, Class<? extends Service> service) throws IOException {
		listen_socket = new ServerSocket(port);
		this.service = service;
	}

	// Le serveur ecoute et accepte les connections.
	// pour chaque connection, il cree un ServiceInversion, 
	// qui va la traiter.
	public void run()
	{
		while(true)
		{
			try {
				Constructor<? extends Service> constructorAvesSocket = service.getConstructor(java.net.Socket.class);
				Service service = constructorAvesSocket.newInstance(listen_socket.accept());
				new Thread(service).start();
			} catch(IOException e) {
				System.err.println("Probleme lors de la connexion au port " + listen_socket);
				e.printStackTrace();
			}
			catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	 // restituer les ressources --> finalize
	protected void finalize() throws Throwable {
		try {this.listen_socket.close();} catch (IOException e1) {}
	}

}
