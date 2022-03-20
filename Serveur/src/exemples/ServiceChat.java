package brette;


import bri.Service;
import utilisateurs.Utilisateur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class ServiceChat implements Service {

    private static final HashMap<String, ArrayList<String>> messages = new HashMap<>();
    static {
        messages.put("sofiane", new ArrayList<>(Collections.singletonList("Salut bg tu vas bien ?")));
    }

    private static List<Utilisateur> utilisateurs = new ArrayList<>(Arrays.asList(
       new Utilisateur("sofiane", "bounezou"),
       new Utilisateur("elyes", "terki")
    ));
    private final Socket client;

    public ServiceChat(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            String username = null;
            String response = null;
            String[] splits = null;

            out.println("Bienvenue dans le service de messagerie !##1. Connexion##2. Inscription");


            int choix = Integer.parseInt(in.readLine());
            switch (choix)
            {
                case 1: {
                    out.println("Connectez vous comme ceci : login/motdepasse");
                    response = in.readLine();
                    splits = response.split("/");

                    if(splits.length != 2) {
                        out.println("[ServiceChat] Vous n'avez pas rentré les bonnes informations de connexion.##Deconnexion...");
                        client.close();
                    }

                    for(Utilisateur u : ServiceChat.utilisateurs) {
                        if (u.getLogin().equals(splits[0]) && u.getPassword().equals(splits[1])) {
                            username = u.getLogin();
                            break;
                        }
                    }

                    if(username == null) {
                        out.println("Utilisateur introuvable.##Deconnexion ...");
                        client.close();
                    }
                    break;
                }
                case 2: {
                    out.println("Inscrivez-vous comme ceci : login/motdepasse");
                    response = in.readLine();
                    splits = response.split("/");

                    if(splits.length != 2) {
                        out.println("[ServiceChat] Vous n'avez pas rentré les bonnes informations d'inscription.##Deconnexion...");
                        client.close();
                    }

                    for(Utilisateur u : ServiceChat.utilisateurs) {
                        if (u.getLogin().equals(splits[0]) && u.getPassword().equals(splits[1])) {
                            out.println("Ce compte existe deja !## Deconnexion...");
                            client.close();
                        }
                    }


                    ServiceChat.utilisateurs.add(new Utilisateur(splits[0], splits[1]));
                    username = splits[0];

                    break;
                }

            }




            while(true)
            {
                out.println("1. Lire vos messages##2. Envoyer des messages##3. Quitter le service");

                try {
                    switch (Integer.parseInt(in.readLine()))
                    {
                        case 1:
                            lireMessages(out, username);
                            System.out.println("Fini lecture de message");
                            break;
                        case 2:
                            envoyerMessages(in, out, username);
                            System.out.println("Fini envoie de message");
                            break;
                        case 3:
                        default:
                            out.println("[ServiceChat] Fin du service de messagerie##Deconnexion ...");
                            client.close();
                    }
                } catch (NumberFormatException e) {
                    System.err.println("[ServiceChat] Entrez un nombre !##Deconnexion ...");
                    out.println("[ServiceChat] Veuillez saisir un nombre valide!##Deconnexion ...");
                    return;
                }
            }

        } catch (IOException e) {
            System.err.println("[ServiceChat] Fin du service de messagerie !");
            e.printStackTrace();
        }
    }

    private void envoyerMessages(BufferedReader in, PrintWriter out, String expediteur) throws IOException {
        while (true) {
            out.println("[ServiceChat] Merci d'entrer l'utilisateur a qui vous souhaitez envoyer un message : ");
            String destinataire = in.readLine();

            for(Utilisateur u : utilisateurs)
            {
                if(u.getLogin().equalsIgnoreCase(destinataire))
                {
                    out.println("[ServiceChat] Entrez votre message puis ecrivez FIN lorsque vous avez termine");

                    StringBuilder sb = new StringBuilder(expediteur + ": ");

                    String message = in.readLine();
                    while(!message.contains("FIN")) { //tant qu'il n'a pas écrit fin
                        out.println("[ServiceChat] Tapez FIN lorsque vous avez fini");
                        sb.append(message);
                        message = in.readLine();
                    }

                    synchronized (messages) {
                        if (messages.containsKey(destinataire) ) {
                            messages.get(destinataire).add(sb.toString());
                            break;
                        }
                        messages.put(destinataire, new ArrayList<>(Collections.singletonList(sb.toString())));
                    }
                }

            }

            out.println("[ServiceChat] Envoyez un nouveau message ? oui/autre");
            String response = in.readLine();
            if(response.startsWith("autre")) break;

        }

    }

    private void lireMessages(PrintWriter out, String username) {
        out.print("[ServiceChat] Vos messages : ##");
        synchronized (messages) {
            if(!messages.containsKey(username)) {
                out.print("Vous n'avez pas de messages##");
            }
            else {
                for (String m : messages.get(username)) {
                    out.print(m + "##");
                }
            }
        }
    }

    protected void finalize() throws Throwable {
        client.close();
    }

    public static String toStringue() {
        return "Service de messagerie";
    }
}
