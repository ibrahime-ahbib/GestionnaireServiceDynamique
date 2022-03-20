package appli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Scanner sc = new Scanner(System.in);
        String tmp;
        String[] lol = {"","",""};
        while(!lol[0].equalsIgnoreCase("BTTP")) {
            System.out.println("CLIENT BTTP : Entrez adresse serveur (BTTP:IP:PORT)");
            tmp = sc.nextLine();
            lol = tmp.split(":");
        }
        try {
            Socket client = new Socket(lol[1],Integer.parseInt(lol[2]));
            System.out.println("Connexion au serveur réussie");
            BufferedReader sIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter sOut = new PrintWriter(client.getOutputStream(), true);
            while(true) {
                if((tmp = sIn.readLine()) == null)
                    break; // fin de connexion par le serveur
                System.out.print(decoder(tmp));
                if((tmp = sc.nextLine()).equals(""))
                    break; // fin de connexion par l'utilisateur
                sOut.println(tmp);
                sOut.flush();
            }
            System.out.println("Fin de connexion");
            client.close();
        } catch (IOException e) {
            System.out.println("Connexion au serveur a échoué");
        }
    }

    private static String decoder(String code) {
        // TODO Auto-generated method stub
        String[] t = code.split("##");
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < t.length; i++)
            sb.append(t[i] + "\n");
        return sb.toString();
    }
}
