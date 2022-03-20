package services;

import utilisateurs.*;
import bri.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ServiceProgrammeur implements Service {
    private static List<Programmeur> programmeurs;
    private final Socket client;
    //URLClassLoader.newInstance(new URL[] { new URL("ftp://127.0.0.1:2121/admin")})
    static {
        try {
            programmeurs = new ArrayList<>(
                     Arrays.asList(
                             new Programmeur("admin",
                                     "admin",
                                     URLClassLoader.newInstance( new URL[] { new URL("ftp://127.0.0.1:2121/") }))
                     )
            );
        } catch (MalformedURLException e) {
            System.out.println("Erreur FTP pour admin");
            e.printStackTrace();
        }
    }

    public ServiceProgrammeur(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            Programmeur programmeur = null;

            out.println("## Bienvenue dans le service des programmeurs : ##1. Connexion##2. Inscription");
            try {
                int choix = Integer.parseInt(in.readLine());

                switch (choix) {
                    case 1: {
                        out.println("[ServiceProgrammeur] Connectez vous comme ceci : login/motdepasse");
                        String[] response = in.readLine().split("/");
                        for (Programmeur p : programmeurs) {
                            if (p.getLogin().equals(response[0]) && p.getPassword().equals(response[1])) {
                                programmeur = p;
                                break;
                            }
                        }

                        if (programmeur == null) {
                            out.println("[ServiceProgrammeur] Programmeur introuvable.##Deconnexion ...");
                            client.close();
                        }

                        break;
                    }
                    case 2: {
                        out.println("[ServiceProgrammeur] Inscrivez-vous comme ceci : login/motdepasse");
                        String[] response = in.readLine().split("/");

                        if (response.length != 2) {
                            out.println("[ServiceProgrammeur] Vous n'avez pas entre les informations comme demande## Deconnexion ...");
                            client.close();
                        }

                        for (Programmeur u : programmeurs) {
                            if (u.getLogin().equals(response[0]) && u.getPassword().equals(response[1])) {
                                out.println("[ServiceProgrammeur] Ce compte existe deja !##Deconnexion ...");
                                client.close();
                            }
                        }


                        out.println("[ServiceProgrammeur] Fournissez l'URL de votre serveur ftp avec ou sans le port (si port 21 par defaut) : ");
                        String url = in.readLine().strip();

                        if (!url.startsWith("ftp://")) {
                            out.println("[ServiceProgrammeur] L'URL est incorrect, deconnexion ...");
                            client.close();
                        }

                        URLClassLoader FTPServer = URLClassLoader.newInstance(new URL[]{new URL(url)});


                        programmeurs.add(new Programmeur(response[0], response[1], FTPServer));
                        programmeur = programmeurs.get(programmeurs.size() - 1);
                        // username = response[0];

                        break;
                    }
                    default: {
                        out.println("[ServiceProgrammeur] Deconnexion ... ");
                        client.close();
                    }
                }

                while(true)
                {
                    out.println("1. Ajouter un service##2. Modifier un service##3. Supprimer un service##4. Modifier le serveur FTP ");

                    switch (Integer.parseInt(in.readLine()))
                    {
                        case 1:
                            ajoutService(in, out, programmeur);
                            System.out.println("Fini ajout de service");
                            break;
                        case 2:
                            miseAJourService(in, out, programmeur);
                            System.out.println("Fini modification de service");
                            break;
                        case 3:
                            supprimerService(in, out, programmeur);
                            System.out.println("Fini supression de service");
                            break;
                        case 4:
                            modifierServeurFTP(in, out, programmeur);
                            System.out.println("Fini maj FTP");
                            break;
                        default:
                            out.println("[ServiceProgrammeur] Fin du service programmeur##Deconnexion ...");
                            client.close();
                    }
                }

            } catch (NumberFormatException e) {
                System.err.println("[ServiceProgrammeur] Le client n'a pas rentré de nombre !");
                out.print("[ServiceProgrammeur] Le client n'a pas rentré de nombre !##Deconnexion ...");
                client.close();
            }
        } catch (IOException  e) {
            System.err.println("[ServiceProgrammeur] Erreur socket durant le Service Programmeur ");
        }
    }

    private void modifierServeurFTP(BufferedReader in, PrintWriter out, Programmeur programmeur) throws IOException {
        out.println("[ServiceProgrammeur] Entrée votre nouvel URL FTP");
        String url = in.readLine();
        if(!url.startsWith("ftp://")) {
            out.println("[ServiceProgrammeur] URL FTP de changement invalide");
        }

        programmeur.setFTPServer(URLClassLoader.newInstance(new URL[] { new URL(url) }));
        out.print("[ServiceProgrammeur] Serveur FTP mis à jour !## ");
    }

    private void supprimerService(BufferedReader in, PrintWriter out, Programmeur programmeur) throws NumberFormatException, IOException {
        out.println("[ServiceProgrammeur] Entrée un numéro de service à supprimer");
        int numService = Integer.parseInt(in.readLine());
        try {
            //La classe n'a plus aucune relation après ces deux lignes, elle est donc déchargée
            programmeur.nettoyageFTPServer();
            ServiceRegistry.supprimerService(numService);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            out.print("Service introuvable##Deconnexion...##");
            client.close();
        }
        out.print("[ServiceProgrammeur] Service désinstallé !##");
    }

    private void miseAJourService(BufferedReader in, PrintWriter out, Programmeur programmeur) throws IOException {
        out.println("[ServiceProgrammeur] Entrez le nom de la classe à mettre à jour (ex: brette.ServiceChat)");
        try {
            String classeString = in.readLine();

            if(!classeString.startsWith(programmeur.getLogin() + ".")) {
                out.println("[ServiceProgrammeur] Le package utilisé ne correspond pas à votre package !##Deconnexion...");
                client.close();
            }

            int numService = ServiceRegistry.getServiceIndex(classeString);

            if(numService == -1) {
                out.println("[ServiceProgrammeur] Le service est introuvable.##Deconnexion...");
                client.close();
            }

            //La classe n'a plus aucune relation après ces deux lignes, elle est donc déchargée
            programmeur.nettoyageFTPServer();
            Class<?extends  Service> nouvelleClasse = programmeur.getFTPServer().loadClass(classeString).asSubclass(Service.class);

            ServiceRegistry.miseAJourService(numService, nouvelleClasse);
        } catch (IndexOutOfBoundsException | NumberFormatException | ClassNotFoundException | ValidationException e) {
            out.print("Service introuvable##Deconnexion...##");
            client.close();
        }
        out.print("[ServiceProgrammeur] Service désinstallé !##");
    }

    private void ajoutService(BufferedReader in, PrintWriter out, Programmeur programmeur) throws IOException {
        out.println("Entrez le nom de votre service sous la forme username.VotreClasse");

        try {
            String classeString = in.readLine();

            if(!classeString.startsWith(programmeur.getLogin() + ".")) {
                out.println("[ServiceProgrammeur] Le package utilisé ne correspond pas à votre package !##Deconnexion...");
                client.close();
            }

            System.out.println(classeString);
            Class<? extends Service> classe =
                    programmeur.getFTPServer().loadClass(classeString).asSubclass(Service.class);
            ServiceRegistry.addService(classe);
            out.print("Service " + classeString + " ajouté avec succès !##");
        } catch (ClassNotFoundException | ValidationException | IOException e) {
            e.printStackTrace();
            System.err.println("[ServiceProgrammeur] La classe n'a pas ete chargee !");
            out.println("[ServiceProgrammeur] La classe n'a pas ete chargee !##Deconnexion...");
            client.close();
        }
    }

    protected void finalize() throws Throwable {
        client.close();
    }
}
