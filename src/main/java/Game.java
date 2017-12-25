import lombok.Getter;
import lombok.Setter;

public class Game {

    @Getter @Setter private int id;
    @Getter @Setter private String name;
    @Getter @Setter private double cost;

    Game() {}
    public Game(int id, String name, double cost) {
        this.id = id;
        this.name = name;
        this.cost = cost;
    }
}
