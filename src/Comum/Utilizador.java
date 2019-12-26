package Comum;

public class Utilizador {
    private int id;
    private String name;
    private String password;

    public Utilizador(String name, String password) {
        this.name = name;
        this.password = password;
        id = -1;
    }

    public Utilizador(int id, String name) {
        this.id = id;
        this.name = name;
        password = null;
    }

    public Utilizador(String name) {
        this.name = name;
        id = -1;
        password = null;
    }

    public int getId() { return id; }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void resetPassword(){ password = "";}

    @Override
    public String toString() {
        return "Utilizador{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
