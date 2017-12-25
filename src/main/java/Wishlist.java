import com.sun.istack.internal.NotNull;
import lombok.NonNull;
import lombok.Setter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.jsoup.Jsoup.connect;

public class Wishlist {

    @Setter private String url = "";

    // Test wishlists
    public static final String chrisURL = "http://steamcommunity.com/profiles/76561198073063461/wishlist/?sort=price";
    public static final String eliURL = "http://steamcommunity.com/id/CombineCrab/wishlist/";

    @NonNull private Document doc;
    private List<Game> wishlist;

    public Wishlist(String url) {
        this.url = url;
        setupConnection();
    }


    private void setupConnection() {
        try {
            if (!url.equalsIgnoreCase("")) {
                this.doc = connect(url).userAgent("Chrome/63.0.3239").get();
            }
            else {
                System.out.println("Please enter an actual URL");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Does all the grunt work
    private void retrieveWishlist() {
        wishlist = new ArrayList<>();

        @NotNull Game game = null;
        boolean validGame = false;
        for (Element s : doc.getAllElements()) {
            // We've found a wishlist game to scrape, get the
            if (s.hasClass("wishlistRow")) {
                game = new Game();
                validGame = true;

                // Strip the 'game_' off of ID for each game (i.e game_50130 becomes 50130). Unique identifier.
                Integer id = Integer.valueOf(s.id().replaceAll("[^\\d.]", ""));

                // Reverse search for active game here to double check legit game?
                game.setId(id);
            }

            if (s.hasClass("price") || s.hasClass("discount_final_price")) {
                if (s.text().toCharArray().length == 0 || s.text().equalsIgnoreCase("")) {
                    validGame = false;
                }
                else if (s.text().trim().equalsIgnoreCase("Free to Play")) {
                    game.setCost(0);
                }
                else {
                    String value = s.text().replaceAll("[^\\d]", "");
                    game.setCost(Integer.parseInt(value));
                }
            }

            // Get the name of each game
            if (s.hasClass("ellipsis") && !s.hasClass("wishlist_added_on")) {
                // Get each name of each game
                if (s.text().toCharArray().length == 0 || s.text().equalsIgnoreCase("")) {
                    validGame = false;
                }
                else {
                    game.setName(s.text());
                }

                // This part comes last when searching DOM so we check here if the game to add has been valid.
                if (validGame) {
                    wishlist.add(game);
                }
                validGame = false;
            }
        }
    }

    private void printWishlist() {
        for (Game g : wishlist) {
            System.out.println(g.toString());
        }
    }

    private Integer getTotalCosts() {
        Integer total = 0;
        for(Game g: wishlist) {
            total += g.getCost();
        }
        return total;
    }

    private void printTotalCosts() {
        System.out.println(getTotalCosts());
    }

    public static void main(String[] args) {
        Wishlist two = new Wishlist(eliURL);
        two.retrieveWishlist();
        two.printWishlist();
    }
}