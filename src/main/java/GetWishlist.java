import static spark.Spark.*;

public class GetWishlist {
    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");
    }
}
