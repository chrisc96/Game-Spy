import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
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
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jsoup.Jsoup.connect;

/**
 * Created by Chris on 26/12/2017.
 */
public class G2A {

    public static final String baseIDUrl = "https://www.g2a.com/lucene/search/filter?&search="; // This will allow us to get the ID of the game
    public static final String basePricingURL = "https://www.g2a.com/marketplace/product/auctions/?id="; // Using the ID we can get the minimum price of the game.
    public static String platform = "";

    List<Game> toFind;
    Map<Game, Game> returnedResults = new HashMap<>();

    public G2A() {}

    public G2A(List<Game> toFind) {
        this.toFind = new ArrayList<>(toFind);
    }

    void getG2APrices() {
        for (Game g: toFind) {
            returnedResults.put(g, getG2APrice(g));
            break;
        }
        System.out.println(returnedResults);
    }

    Game getG2APrice(Game g) {
        Integer idOfGame = getIDFromLucene();
        Integer costOfGame = getCostWithID(idOfGame);

        g.setCost(costOfGame);
        return g;
    }

    public Integer getIDFromLucene() {
        return -1;
    }

    private Integer getCostWithID(Integer idOfGame) {
        return -1;
    }

    /**
     * Given a normal game name, converts it to a g2a search query to specifically request
     * different types of results. I.E global keys, steam key, non steam key etc.
     * @param gameName
     * @return
     */
    String gameNameCheck(String gameName) {
        StringBuilder searchQuery = new StringBuilder(gameName);
        if (!gameName.contains("Global")) {
            searchQuery.append(" Global");
        }
        System.out.println(searchQueryToUrl(searchQuery.toString()));
        return searchQuery.toString();
    }

    private String searchQueryToUrl(String gameName) {
        StringBuilder toReturn = new StringBuilder();
        boolean charAfterSpace = charAfterSpaces(gameName);

        gameName = gameName.replaceAll(" ", "+");
        toReturn = toReturn.append(gameName);
        System.out.println(toReturn.toString());
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
