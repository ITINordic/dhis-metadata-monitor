package zw.mohcc.dhis;

import static spark.Spark.*;

public class DHISMetaDataMonitor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        get("/hello", (req, res) -> "Hello World");

    }
}
