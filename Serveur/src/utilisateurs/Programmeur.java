package utilisateurs;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class Programmeur extends Utilisateur{
    private URLClassLoader FTPServer;

    public Programmeur(String login, String password, URLClassLoader FTPServer) {
        super(login, password);
        this.FTPServer = FTPServer;
    }

    public URLClassLoader getFTPServer() {
        return FTPServer;
    }

    public void setFTPServer(URLClassLoader FTPServer) {
        this.FTPServer = FTPServer;
    }

    public void nettoyageFTPServer() {
        /*
                Ici afin de décharger la classe supprimé on procède en 4 étapes:
                    1- On récupère le tableau d'URL (où l'url FTP est contenu)
                    2- On met à null notre URLClassLoader afin que notre System.gc s'en débarasse et décharge donc les classes loadées
                    (il faut absoluement au prélable que la classe supprimée ou mis à jour n'est plus aucun lien : ni avec l'URLClassLoader
                    ni avec ServiceRegistry)
                    3- On récréé une instance d'URLClassLoader afin de pouvoir loader à nouveau des classes, les classes chargées précedemment sont
                    toujours existantes puisqu'ils ont un lien avec ServiceRegistry.
        */
        URL[] urls = this.FTPServer.getURLs();
        this.FTPServer = null;
        System.gc();
        this.FTPServer = URLClassLoader.newInstance(urls);
    }
}
