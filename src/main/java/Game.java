import lombok.Getter;
import lombok.Setter;

public class Game {

    @Getter @Setter private int id;
    @Getter @Setter private String name;
    @Getter @Setter private Integer cost; // In NZD cents

    Game() {}
    public Game(int id, String name, Integer cost) {
        this.id = id;
        this.name = name;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cost in NZD cents=" + cost +
                '}';
    }
}
