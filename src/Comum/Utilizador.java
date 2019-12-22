package Comum;

public class Utilizador {
    private int id;
    private String name;
    private String password;

    public Utilizador(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public Utilizador(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Utilizador(String name) {
        this.name = name;
    }

    public int getId() { return id; }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

}
