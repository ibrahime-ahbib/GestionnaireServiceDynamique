package utilisateurs;


public class Utilisateur  {
    private static int compteur = 0;

    private int id;
    private String login;
    private String password;

    public Utilisateur(String login, String password) {
        this.id = compteur++;
        this.login = login;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilisateur)) return false;
        Utilisateur u = (Utilisateur) o;
        return this.login.equals(u.login) && this.password.equals(u.password);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
