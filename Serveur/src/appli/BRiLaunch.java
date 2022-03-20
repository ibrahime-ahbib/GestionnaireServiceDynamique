package appli;

import java.io.IOException;

import bri.*;
import services.ServiceAmateur;
import services.ServiceProgrammeur;

public class BRiLaunch {

	// chargement dynamique d'un jarfile contenant une seule classe
	// cette classe implémente l'interface VerySimple

	private static final int PORT_AMA = 3000;
	private static final int PORT_PROG = 5000;

	public static void main(String[] args) throws IOException {
		System.out.println("## Lancement du serveur au port 3000");
		System.out.println("## Lancement du serveur au port 5000");
		new Thread(new ServeurBRi(PORT_AMA, ServiceAmateur.class)).start();
		new Thread(new ServeurBRi(PORT_PROG, ServiceProgrammeur.class)).start();

	}
}
