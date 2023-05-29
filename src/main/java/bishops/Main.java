package bishops;

import javafx.application.Application;


import static bishops.DatabaseController.createDatabaseDir;
import static bishops.DatabaseController.createDatabase;

public class Main {

    public static void main(String[] args) {
        createDatabaseDir();
        createDatabase();
        Application.launch(BishopsApplication.class,args);
    }
}

