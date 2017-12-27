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

    StringBuilder baseUrl = new StringBuilder("https://www.g2a.com/?search=");

    public G2A() {}

    public G2A(List<Game> toFind) {
        this.toFind = toFind;
    }

    String nameToUrlAppend(String gameName) {
        String url = baseUrl + gameName.replaceAll(" ", "%");
        StringBuilder resultURL = new StringBuilder(url);

        Pattern pattern = Pattern.compile("/[-.0-9]+/");
        Matcher m = pattern.matcher(url);
        resultURL.append(url);
        while (m.find(baseUrl.length())) {
            
        }


        System.out.println();

        return baseUrl + gameName.replaceAll(" ", "%");
    }

}
