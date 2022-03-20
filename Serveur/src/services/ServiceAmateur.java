package services;

import bri.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.net.Socket;


public class ServiceAmateur implements Service {
    private final Socket client;

    public ServiceAmateur(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            out.println("Bonjour et bienvenue sur notre serveur ! Voici les services disponibles :"+ServiceRegistry.toStringue());

            int choix = Integer.parseInt(in.readLine());
            Class<? extends Service> service = ServiceRegistry.getServiceClass(choix);
            Constructor<? extends Service > constructor = service.getConstructor(Socket.class);
            new Thread(constructor.newInstance(client)).start();

        } catch(IOException e) { System.err.println("Erreur socket durant le Service Amateur "); }
        catch (NumberFormatException e) { System.err.println("Vous n'avez pas entré de nombre ! Arrête du serveur..."); }
        catch (Exception e) { e.printStackTrace(); }
    }

    protected void finalize() throws Throwable {
        client.close();
    }
}
