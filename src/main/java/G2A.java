import com.sun.istack.internal.NotNull;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jsoup.Jsoup.connect;

/**
 * Created by Chris on 26/12/2017.
 */
public class G2A {

    // URL format is baseUrl + platform + endUrl + query (game name converted correctly)
    public static final String baseUrl = "https://www.g2a.com/?";
    public static String platform = "";

    // Platforms. Incorporate into platform selection for queries
    public static final String NOPLAT = "";
    public static final String STEAM = "p=17&";
    public static final String ORIGIN = "p=16&";
    public static final String UPLAY = "p=24&";

    public static final String endURL = "search=";

    List<Game> toFind;
    Map<Game, Game> returnedResults = new HashMap<>();

    public G2A() {}

    public G2A(List<Game> toFind) {
        this.toFind = toFind;
    }

    void getG2APrices() {
        for (Game g: toFind) {
            returnedResults.put(g, getG2APrice(g));
        }
        System.out.println(returnedResults);
    }

    Game getG2APrice(Game g) {
        try {
            Document doc = connect(gameNameToUrl(g.getName())).userAgent("Chrome/63.0.3239").get();
            boolean productsMatchName = false;
            for (Element e: doc.getAllElements()) {
                if (e.id().equals("jq_noproducts")) {
                    // If the no products div isn't visible, there must be results
                    String style = e.attr("style"); // getting the inline style element
                    if (style.contains("none")) {
                        productsMatchName = true;
                        break;
                    }
                }
            }
            if (productsMatchName) {
                @NotNull Game game = null;
                Element e = doc.body().select("div#tmp_filters_products").first();
                for (Element child : e.getAllElements()) {
                    // Found a result, lets only add one result (via most popular for now)
                    if (child.hasClass("product_name_link")) {
                        game = new Game();
                        game.setId(g.getId());
                        game.setName(e.text());
                    }

                    if (child.hasClass("mp-pi-price-min")) {
                        String value = e.text().replaceAll("[^\\d]", "");
                        game.setCost(Integer.parseInt(value));
                        return game;
                    }
                }
            }
            // No results that matched the game to search against... oh no
            else {
                return null;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Given a normal game name, converts it to a g2a search query to specifically request
     * different types of results. I.E global keys, steam key, non steam key etc.
     * @param gameName
     * @return
     */
    String gameNameToUrl(String gameName) {
        StringBuilder searchQuery = new StringBuilder(gameName);
        if (!gameName.contains("Global")) {
            searchQuery.append(" Global");
        }
        //System.out.println(searchQueryToUrl(searchQuery.toString()));
        return searchQueryToUrl(searchQuery.toString());
    }

    private String searchQueryToUrl(String gameName) {
        StringBuilder toReturn = new StringBuilder(baseUrl + platform + endURL);
        boolean charAfterSpace = charAfterSpaces(gameName);
        
        if (charAfterSpace) {
            toReturn = toReturn.append(gameName.replaceAll(" ", "%20"));
        }
        else {
            toReturn = toReturn.append(gameName.replaceAll(" ", ""));
        }
        return toReturn.toString();
    }

    private boolean charAfterSpaces(String gameName) {
        boolean somethingAfterSpace = false;
        for (int i = 1; i < gameName.length(); i++) {
            if (Character.isSpaceChar(gameName.charAt(i)) && !Character.isSpaceChar(gameName.charAt(i-1))) {
                // If there is a non space character that comes after this space character
                if (nonSpace(gameName, i)) {
                    somethingAfterSpace = true;
                    break;
                }
            }
        }
        return somethingAfterSpace;
    }

    private boolean nonSpace(String gameName, int i) {
        boolean nonSpace = false;
        for (int j = i; j < gameName.length(); j++) {
            if (!Character.isSpaceChar(gameName.charAt(j))) {
                nonSpace = true;
                break;
            }
        }
        return nonSpace;
    }
}
