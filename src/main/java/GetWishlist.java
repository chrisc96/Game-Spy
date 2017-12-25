import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;
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
    private String url = "http://steamcommunity.com/profiles/76561198073063461/wishlist";

    private GetWishlist() {
        if (!url.equalsIgnoreCase("")) try {
            Document doc = Jsoup.connect(url).userAgent("Chrome/63.0.3239").get();
            for (Element s : doc.getAllElements()) {
                // We've found each wishlist entry to scrape
                if (s.hasClass("wishlistRow")) {
                    Game game = new Game();

                    // Strip the 'game_' off of ID for each game (i.e game_50130 becomes 50130). Unique identifier.
                    Integer id = Integer.valueOf(s.id().replaceAll("[^\\d.]", ""));
                    game.setId(id);

                    // Get each name of each game
                    Elements children = s.children();
                    game.setName(children.select(".ellipsis").first().text());
                    System.out.println(game.getName());


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new GetWishlist();
    }
}