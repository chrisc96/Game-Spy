import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Chris on 26/12/2017.
 */
public class G2A {

    // Need some way to search via search bar.
    // Once searched. Sorted by most popular. We need to ascertain which one to choose. This is involved...

    // We should ignore any titles with DLC references in them
    // Ideally we would make it interactive so they get to choose which titles they want. But then that makes it pretty awkward.

    // We could select programmatically select one, show it in the list, and have a button for (this isn't the right one).
    // This goes and shows the entire list and they select the correct one and it updates the list with the price and link. GG.

    // url = https://www.g2a.com/?search= + rest
    // e.g: https://www.g2a.com/?search=xda%20hello%201%202%203
    // so % for every space. For a number, it's 20 + number value?
    // i.e titanfall 2 is:
        // https://www.g2a.com/?search=titanfall%202
    // i.e titanfall 10 is:
        // https://www.g2a.com/?search=titanfall%2010
            // So concatenate the numbers rather than add.

    // No results = id: jq_noproducts class: notice_msg and display != none
    // Block of results are contained in: search-results class.
    // a result has class: product-small2

    // name is product_name_link, get text of this.
    // price of item at cheapest is mp-pi-price-min

    List<Game> toFind;
    Map<Game, List<Game>> returnedResults = new HashMap<>();

    String baseUrl = "https://www.g2a.com/?search=";

    public G2A() {}

    public G2A(List<Game> toFind) {
        this.toFind = toFind;
    }

    String nameToUrlAppend(String gameName) {

        // titanfall 2 = titanfall%202
        //; titanfall      c = titanfall%20%20%20%20%20%20c
        // titanfall 10 20 30 = titanfall%2010%2020%2030
        // titanfall hello 1 2 3 = titanfall%20hello%201%202%203
        // titanfall      = titanfall
        // titanfall ! = titanfall%20!

        // If nothing present after first space, then strip text down to just initial text.
        // Else replace all spaces with %20 then whatever

        StringBuilder toReturn = new StringBuilder(baseUrl);
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
