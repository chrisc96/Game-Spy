/**
 * Created by Chris on 26/12/2017.
 */
public class GameCompare {

    public static void main(String[] args) {
        SteamWishlist two = new SteamWishlist(SteamWishlist.chrisURL);
        two.retrieveWishlist();
        two.printWishlist();

        G2A g2a = new G2A(two.wishlist);
        g2a.getG2APrices();
    }
}
