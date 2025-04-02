import java.util.ArrayList;

public class Team {
    private int id;
    private String name;
    private ArrayList<Player> players;

    public Team(int id, String name, ArrayList<Player> players) {
        this.id = id;
        this.name = name;
        this.players = players;
    }
}
