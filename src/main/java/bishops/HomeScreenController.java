package bishops;

import bishops.model.Highscore;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bishops.DatabaseController.getJdbiDatabasePath;

public class HomeScreenController {


    @FXML
    private TextField name;

    public final static Highscore highscore = new Highscore();

    public final static List<Highscore> highscoresList = new ArrayList<>();

    @FXML
    private void initialize(){
        Bindings.bindBidirectional(name.textProperty(), highscore.nameProperty());
        highscoresList.clear();
    }

    @FXML
    private void onNewGame(ActionEvent event) throws IOException{
        Logger.debug("Click on New Game");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/Board.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML private void onQuit(ActionEvent event) throws IOException{
        Logger.debug("Click on Quit");
        Logger.debug("Exiting...");
        Platform.exit();
    }

    @FXML
    private void onAbout(ActionEvent event) throws IOException{
        Logger.debug("Kattints a leírásra!");
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("Leírás");
        about.setHeaderText("Bishops Application");
        about.setContentText("""
                Egy 5 sorból és 4 oszlopból álló sakktábla legfelső sorába 4 sötét, legalsó sorába 4 világos futót helyezünk. Cseréljük meg a figurákat, olyan módon,
                hogy azok futólépésben léphetnek, továbbá nem léphetnek olyan mezőre,
                amelyet ellentétes színű figura üt!""");
        about.showAndWait();
        Logger.debug("Kattints");
    }

    @FXML
    private void onLeaderboard(ActionEvent event) throws IOException{
        Jdbi jdbi = Jdbi.create(getJdbiDatabasePath());
        try (Handle handle = jdbi.open()){
            var idCount = handle.createQuery("SELECT COUNT (*) FROM Highscores").mapTo(Integer.class).one();
            idCount = (idCount < 6 ? idCount : 5);
            for (int id = 0; id < idCount; id++) {
                var name = handle.createQuery("""
                            WITH BS AS(SELECT ID,NAME,SCORE FROM HIGHSCORES ORDER BY SCORE LIMIT 5)
                            SELECT NAME FROM BS LIMIT 1 OFFSET %s
                            """.formatted(id)).mapTo(String.class).one();
                var score = handle.createQuery("""
                            WITH BS AS(SELECT ID,NAME,SCORE FROM HIGHSCORES ORDER BY SCORE LIMIT 5)
                            SELECT SCORE FROM BS LIMIT 1 OFFSET %s
                            """.formatted(id)).mapTo(Integer.class).one();
                Highscore highscore = new Highscore();
                highscore.setName(name);
                highscore.setScore(score);
                highscoresList.add(highscore);
            }
            }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/Leaderboard.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
        Logger.debug("Kattints a ranglistára");
        }

    }
