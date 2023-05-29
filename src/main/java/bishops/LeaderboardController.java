package bishops;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static bishops.DatabaseController.getJdbiDatabasePath;

public class LeaderboardController {
    private HomeScreenController homeScreenController;

    @FXML
    private Label name1;
    @FXML
    private Label name2;
    @FXML
    private Label name3;
    @FXML
    private Label name4;
    @FXML
    private Label name5;
    @FXML
    private Label score1;
    @FXML
    private Label score2;
    @FXML
    private Label score3;
    @FXML
    private Label score4;
    @FXML
    private Label score5;

    private List<Label> names = new ArrayList<>();
    private List<Label> scores = new ArrayList<>();

    @FXML
    private void initialize(){
        Collections.addAll(names,name1,name2,name3,name4,name5);
        Collections.addAll(scores,score1,score2,score3,score4,score5);
        for (int i = 0; i < HomeScreenController.highscoresList.size(); i++) {
            names.get(i).setText(HomeScreenController.highscoresList.get(i).getName());
            scores.get(i).setText(String.valueOf(HomeScreenController.highscoresList.get(i).getScore()));
        }
    }

    @FXML
    private void onBack(ActionEvent event) throws IOException{
        Stage stage = (Stage)  ((MenuItem)event.getSource()).getParentPopup().getOwnerWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/HomeScreen.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
        Logger.debug("Kattints vissza!");
    }

    @FXML
    private void onResetLeaderboard(ActionEvent event) throws IOException {
        Logger.debug("Kattints");
        Alert quit = new Alert(Alert.AlertType.CONFIRMATION);
        quit.setTitle("Ranglista újra");
        quit.setHeaderText("Biztos vissza akarod állítani a ranglistát?");
        quit.setContentText("Minden korábbi eredményed elvész!");
        Optional<ButtonType> result = quit.showAndWait();
        if (result.get() == ButtonType.OK) {
            Jdbi jdbi = Jdbi.create(getJdbiDatabasePath());
            try (Handle handle = jdbi.open()) {
                handle.execute("DELETE FROM HIGHSCORES");
            }
            HomeScreenController.highscoresList.clear();
            for(var name:names){
                name.setText("");
            }
            for(var score:scores){
                score.setText("");
            }
            Logger.debug("Kattints az OK gombra");
        }else {
            Logger.debug("Kattints a Mégse gombra");
        }
    }
}

