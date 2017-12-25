import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class GetWishlist {

    // http://steamcommunity.com/id/ + "CombineCrab" + /wishlist/
    // OR
    // http://steamcommunity.com/profiles/ + "76561198073063461" + /wishlist

    // Name of game (class) = ellipsis

    // If gameListPriceData div class has child of discount_block then it must be on discount so,
    // get the value via finding the class: discount_final_price
    // Else it must not be on sale so get value via the class: price

    // My connection
    private String url = "http://steamcommunity.com/profiles/76561198073063461/wishlist/?sort=price";
    private List<Game> wishlist;


    private GetWishlist() {
        if (!url.equalsIgnoreCase("")) {
            try {
                // Setup connection
                wishlist = new ArrayList<>();
                Game game = null;
                boolean gameCredsValid = true; // If any of the parsing goes wrong (i.e gameCredsValid = false), the game is not added

                // Connect to url
                Document doc = Jsoup.connect(url).userAgent("Chrome/63.0.3239").get();
                for (Element s : doc.getAllElements()) {
                    // We've found a wishlist game to scrape
                    if (s.hasClass("wishlistRow")) {
                        game = new Game();

                        // Strip the 'game_' off of ID for each game (i.e game_50130 becomes 50130). Unique identifier.
                        Integer id = Integer.valueOf(s.id().replaceAll("[^\\d.]", ""));
                        game.setId(id);
                    }

                    if (s.hasClass("price") || s.hasClass("discount_final_price")) {
                        if (game != null) {
                            if (s.text().trim().equalsIgnoreCase("Free to Play")) {
                                game.setCost(0);
                            }
                            else {
                                String value = s.text().replaceAll("[^\\d]", "");
                                game.setCost(Integer.parseInt(value));
                            }
                        }
                    }

                    if (s.hasClass("ellipsis") && !s.hasClass("wishlist_added_on")) {
                        // Get each name of each game
                        if (game != null && !s.text().equalsIgnoreCase("")) {
                            game.setName(s.text());
                            wishlist.add(game);
                        }
                        game = null; // This part comes last when searching the DOM so we reset the current game back to null
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Game g : wishlist) {
            System.out.println(g.toString());
        }

    }

    private void parseGameID() {

    }

    public static void main(String[] args) {
        new GetWishlist();
    }
}