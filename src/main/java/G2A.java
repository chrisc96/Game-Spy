import jdk.nashorn.internal.parser.JSONParser;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
        Integer idOfGame = getIDFromLucene(g.getName());
        Integer costOfGame = getCostWithID(idOfGame);

        if (idOfGame == -1 || costOfGame == -1) return null;

        g.setCost(costOfGame);
        return g;
    }

    public Integer getIDFromLucene(String gameName) {
        gameName = gameNameModify(gameName);
        gameName = gameNameToURL_Lucene(gameName);
        try {
            JSONObject json = new JSONObject(IOUtils.toString(new URL(gameName), Charset.forName("UTF-8")));

            JSONArray docs = (JSONArray) json.get("docs");
            JSONObject info = (JSONObject) docs.get(0);

            for (Map.Entry<String, Object> entry : info.toMap().entrySet()) {
                if (entry.getKey().equalsIgnoreCase("id")) {
                    return (Integer) entry.getValue();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * For G2A, lucene requires that all spaces become '+' and
     * all special characters get converted to their hexidecimal conversions
     * to be parsed properly. This method converts a String query to a proper
     * lucene URL.
     * @param gameName
     * @return
     */
    private String gameNameToURL_Lucene(String gameName) {
        StringBuilder url = new StringBuilder(baseIDUrl);

        specialCharsToHex(gameName, url);
        url = new StringBuilder(url.toString().replaceAll(" ", "+"));

        return url.toString();
    }

    /**
     * Alters a String by removing all special characters and converting them to
     * hexicedimal variants. Used in Lucene to convert string searches to valid inputs
     * @param gameName
     * @param url
     */
    private void specialCharsToHex(String gameName, StringBuilder url) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        for (Character c : gameName.toCharArray()) {
            String toAppend;
            if (p.matcher(c.toString()).matches()) {
                toAppend = "%" + toHex(c.toString());
            }
            else {
                toAppend = c.toString();
            }
            url.append(toAppend);
        }
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
    String gameNameModify(String gameName) {
        StringBuilder searchQuery = new StringBuilder(gameName);
        if (!gameName.contains("Global")) {
            searchQuery.append(" Global");
        }
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

    public String toHex(String arg) {
        byte[] bytes = arg.getBytes(StandardCharsets.UTF_8);
        return DatatypeConverter.printHexBinary(bytes);
    }

}
