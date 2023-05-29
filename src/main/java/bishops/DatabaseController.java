package bishops;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.tinylog.Logger;

import java.io.File;

public class DatabaseController {

    public static String getUsersHomeDir() {
        String users_home = System.getProperty("user.home");
        return users_home.replace("\\", "/");
    }

    public static void createDatabaseDir(){
        String myDirectory = ".BishopsDataBase";
        String path = getUsersHomeDir() + File.separator + myDirectory ;

        if (new File(path).mkdir()) {
            Logger.debug("Könyvtár létrehozva!");
        }else{
            Logger.debug("Könyvtár már létezik!");
        }
    }

    public static String getDatabaseDirPath(){
        String myDirectory = ".BishopsDataBase";
        String path = getUsersHomeDir() + File.separator + myDirectory ;
        return path;
    }

    public static String getJdbiDatabasePath(){
        String filePath = "jdbc:h2:file:" + getDatabaseDirPath() + File.separator + "Highscores.mv.db";
        String jdbiPath = filePath.substring(0,filePath.length()-6);
        return jdbiPath;
    }

    public static void createDatabase() {
        if (!checkForDatabase()) {
            Jdbi jdbi = Jdbi.create(getJdbiDatabasePath());

            try (Handle handle = jdbi.open()) {
                handle.execute("""
                CREATE TABLE HIGHSCORES(
                    ID NUMBER PRIMARY KEY,
                    NAME VARCHAR2(30),
                    SCORE NUMBER
                )
            """);
            }
            Logger.debug("Adatbázis létrehozva!");
        } else {
            Logger.debug("Adatbázis már létezik!");
        }
    }


    public static boolean checkForDatabase() {
        String filePath = getDatabaseDirPath();
        String file = "Highscores.mv.db";
        File databaseFile = new File(filePath, file);
        return databaseFile.exists();
    }

}

