/**
 * Created by Chris on 26/12/2017.
 */
public class GameCompare {

    public static void main(String[] args) {
        Wishlist two = new Wishlist(Wishlist.eliURL);
        two.retrieveWishlist();
        two.printWishlist();

        G2A g2 = new G2A();
        g2.nameToUrlAppend("titanfall 2");
    }
}
