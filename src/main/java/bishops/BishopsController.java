package bishops;

import bishops.model.*;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Color;
import java.util.stream.Collectors;
import javafx.stage.Stage;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.tinylog.Logger;
import javafx.scene.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import bishops.HomeScreenController;

import static bishops.DatabaseController.getJdbiDatabasePath;

public class BishopsController {

    private enum SelectionPhase {
        SELECT_FROM,
        SELECT_TO;

        public SelectionPhase alter() {
            return switch (this) {
                case SELECT_FROM -> SELECT_TO;
                case SELECT_TO -> SELECT_FROM;
            };
        }
    }

    private SelectionPhase selectionPhase = SelectionPhase.SELECT_FROM;

    private List<Position> selectablePositions = new ArrayList<>();

    private Position selected;

    private List<Node> circleNodes = new ArrayList<>();

    private BishopsModel model = new BishopsModel();

    private List<List<Position>> modelStates = new ArrayList<>();

    private int gameStateCount;

    private HomeScreenController homeScreenController;

    @FXML
    private GridPane board;

    @FXML
    private void initialize() {
        createBoard();
        createPieces();
        setSelectablePositions();
        showSelectablePositions();
        gameStateCount = 0;
        modelStates.add(model.getPiecePositions());
    }


    private void createBoard() {
        for (int i = 0; i < board.getRowCount(); i++) {
            for (int j = 0; j < board.getColumnCount(); j++) {
                var square = createSquare();
                square.getStyleClass().add((i + j) % 2 == 0 ? "modernLight" : "modernDark");
                board.add(square, j, i);
            }
        }
    }
    private StackPane createSquare() {
        var square = new StackPane();
        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }
  
    private void createPieces() {
        for (int i = 0; i < model.getPieceCount(); i++) {
            model.positionProperty(i).addListener(this::piecePositionChange);
            var piece = createPiece(model.getPieceType(i) == PieceType.BLACK ? Color.BLACK : Color.WHITE);
            piece.setStroke(model.getPieceType(i) == PieceType.BLACK ? Color.WHITE : Color.BLACK);
            getSquare(model.getPiecePosition(i)).getChildren().add(piece);
        }
    }

     private SVGPath createPiece(Color color) {
            SVGPath piece = new SVGPath();
            piece.setContent("M 30.768259,10.924369 C 29.036791,10.924369 27.639723,12.318199 27.639728,14.034978 C 27.639728,15.751757 29.036791,17.136968 30.768259,17.136968 C 32.499728,17.136968 33.90548,15.751757 33.905484,14.034978 C 33.905484,12.318199 32.499724,10.924369 30.768259,10.924369 z M 30.768259,17.136968 C 16.966722,22.113135 16.09987,34.554956 23.000639,36.421019 C 23.000639,36.421019 20.371908,42.859987 20.371908,42.859987 C 23.273129,46.847593 38.021763,47.07119 41.283677,42.859987 C 41.283677,42.859987 38.544569,36.421019 38.544569,36.421019 C 45.445338,34.554956 44.569801,22.113135 30.768259,17.136968 z M 30.768259,46.278933 C 27.98553,48.26015 25.3277,51.42112 21.248719,49.001746 C 17.5456,46.805308 9.8482757,52.155898 9.0467381,52.91203 C 9.0467381,52.91203 11.887168,55.890865 11.887168,55.890865 C 17.815198,51.443893 24.776012,55.69915 30.768259,51.735765 C 36.265761,55.858148 44.633223,50.967288 51.477417,56.079359 C 51.477417,56.079359 54.953262,52.91203 54.953262,52.91203 C 54.953262,52.91203 44.505299,48.078674 41.030865,49.001746 C 35.00946,50.601489 33.429838,48.173892 30.768259,46.278933 z M 30.772114,23.892683 L 30.772114,30.11289 M 27.635406,27.002784 L 33.908831,27.002784 M 22.68221,36.572345 C 26.864494,34.461462 34.558038,34.900124 38.740323,36.572345 M 21.030855,42.853497 C 27.073657,39.471968 33.653072,39.048245 40.28131,42.665764");
            piece.setFill(color);
            piece.setScaleX(1.5);
            piece.setScaleY(1.5);
            return piece;
        }
     @FXML
    private void handleMouseClick(MouseEvent event) {
        var square = (StackPane) event.getSource();
        var row = GridPane.getRowIndex(square);
        var col = GridPane.getColumnIndex(square);
        var position = new Position(row, col);
        Logger.debug("Kattints {}", position);
        handleClickOnSquare(position);
    }
    private void handleClickOnSquare(Position position) {
        if (selectionPhase == SelectionPhase.SELECT_FROM) {
            if (selectablePositions.contains(position)) {
                selectPosition(position);
                alterSelectionPhase();
            }
        } else if (selectionPhase == SelectionPhase.SELECT_TO) {
            if (selectablePositions.contains(position)) {
                var pieceNumber = model.getPieceNumber(selected).getAsInt();
                var direction = Directions.of(position.row() - selected.row(), position.col() - selected.col());
                Logger.debug("Moving piece {} {} position", pieceNumber, direction);
                trimModelStates();
                model.move(pieceNumber, direction);
                deselectSelectedPosition();
                alterSelectionPhase();
                gameStateCount++;
                modelStates.add(model.getPiecePositions());
            } else {
                Logger.debug("Deselecting piece");
                deselectSelectedPosition();
                alterSelectionPhase();
            }
        }
    }
    private void alterSelectionPhase() {
        selectionPhase = selectionPhase.alter();
        hideSelectablePositions();
        setSelectablePositions();
        showSelectablePositions();
    }
    private void selectPosition(Position position) {
        selected = position;
        showSelectedPosition();
    }

    private void showSelectedPosition() {
        var square = getSquare(selected);
        square.getStyleClass().add("selected");
    }
    private void trimModelStates() {
        while (gameStateCount != modelStates.size() - 1) {
            modelStates.remove(modelStates.size() - 1);
        }
    }
    private void deselectSelectedPosition() {
        hideSelectedPosition();
        selected = null; //kijelolt pozicio nullra allitja 
    }
    private void hideSelectedPosition() {
        var square = getSquare(selected);
        square.getStyleClass().remove("selected");
    }

    private void setSelectablePositions() {
        selectablePositions.clear();
        switch (selectionPhase) {
            case SELECT_FROM -> selectablePositions.addAll(model.getPiecePositions());
            case SELECT_TO -> {
                var pieceNumber = model.getPieceNumber(selected).getAsInt();
                selectablePositions.addAll(
                        model.getValidMoves(pieceNumber)
                                .stream()
                                .map(selected::moveTo)
                                .collect(Collectors.toList())
                );
            }
        }
    }

    private void showSelectablePositions() {
        if (selectionPhase == SelectionPhase.SELECT_TO){
            for (var selectablePosition : selectablePositions) {
                var square = getSquare(selectablePosition);
                var circle = createCircle();
                square.getChildren().add(circle);
                circleNodes.add(circle);
            }
        }
    }

    private Circle createCircle() {
        var circle = new Circle(10);
        circle.setFill(Color.valueOf("#485238"));
        return circle;
    }

    private void hideSelectablePositions() {
        selectablePositions.forEach(selectablePosition -> {
            var square = getSquare(selectablePosition);
            square.getChildren().removeAll(circleNodes);
        });
        circleNodes.clear();
    }

    private StackPane getSquare(Position position) {
        return board.getChildren().stream()
                .filter(child -> GridPane.getRowIndex(child) == position.row() && GridPane.getColumnIndex(child) == position.col())
                .map(child -> (StackPane) child)
                .findFirst()
                .orElseThrow(AssertionError::new);
    }
    private void piecePositionChange(ObservableValue<? extends Position> observable, Position oldPosition, Position newPosition) {
        Logger.debug("Move {} -> {}", oldPosition, newPosition);
        StackPane oldSquare = getSquare(oldPosition);
        StackPane newSquare = getSquare(newPosition);
        newSquare.getChildren().addAll(oldSquare.getChildren());
        oldSquare.getChildren().clear();
        onGoal();
    }
   @FXML
    private void onModernView() {
        int i = 0;
        int j = 0;
        for (var square : board.getChildren()) {
            if (i % 4 == 0) {
                i = 0;
                j++;
            }
            StackPane stackPane = (StackPane) square;
            updateSquareStyle(stackPane, i, j);
            i++;
        }
        Logger.debug("Modern");
    }
    private void updateSquareStyle(StackPane square, int row, int column) {
        square.getStyleClass().removeAll("modernLight", "modernDark");
        if (row % 2 == 0) {
            if (column % 2 == 0) {
                square.getStyleClass().add("modernLight");
            } else {
                square.getStyleClass().add("modernDark");
            }
        } else {
            if (column % 2 == 0) {
                square.getStyleClass().add("modernDark");
            } else {
                square.getStyleClass().add("modernLight");
            }
        }
    }
 
    @FXML
    private void onNewGame(ActionEvent event) throws IOException {
        Alert newGameConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        newGameConfirmation.setTitle("Új játék");
        newGameConfirmation.setHeaderText("Biztos, hogy újra akarod kezdeni a játékot?");
        newGameConfirmation.setContentText("Minden korábbi eredményed elvész!");

        Optional<ButtonType> result = newGameConfirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Logger.debug("Új játék indítása...");

            model.restart();

            for (var position : model.getPiecePositions()) {
                getSquare(position).getChildren().clear();
            }

            modelStates.clear();
            gameStateCount = 0;

            createPieces();

            modelStates.add(model.getPiecePositions());

            switch (selectionPhase) {
                case SELECT_FROM -> setSelectablePositions();
                case SELECT_TO -> {
                    deselectSelectedPosition();
                    alterSelectionPhase();
                }
            }
        }
    }

    @FXML
    private void onQuitGame(){
        Alert quit = new Alert(Alert.AlertType.CONFIRMATION);
        quit.setTitle("Kilépés");
        quit.setHeaderText("Biztos ki akarsz lépni?");
        quit.setContentText("Minden korábbi eredményed elvész!");
        Optional<ButtonType> result = quit.showAndWait();
        if (result.get() == ButtonType.OK){
            Logger.debug("Kilépés...");
            Platform.exit();
        }
    }

    private void onGoal() {
        if (model.isGoal()) {
            HomeScreenController.highscore.setScore(gameStateCount);
            Jdbi jdbi = Jdbi.create(getJdbiDatabasePath());
            try(Handle handle = jdbi.open()){
                var id = handle.createQuery("SELECT COUNT (*) FROM Highscores").mapTo(Integer.class).one()+1;
                handle.execute("INSERT INTO Highscores VALUES (%s,'%s',%s)".formatted(id,HomeScreenController.highscore.getName(),HomeScreenController.highscore.getScore()));
            }
            ButtonType quit = new ButtonType("Kilépés", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType exit = new ButtonType("Vissza a főoldalra", ButtonBar.ButtonData.OK_DONE);
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                                "A pontszámod: %s".formatted(HomeScreenController.highscore.getScore()),
                                quit,exit
                                );
            alert.initOwner(board.getScene().getWindow());
            alert.setTitle("Gratulálok!!");
            alert.setHeaderText("Gratulálok, %s".formatted(HomeScreenController.highscore.getName()));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == quit){
                Platform.exit();
                Logger.debug("Kilépés...");
            }else {
                try {
                    onExitToMainMenuAlert(alert);
                }catch (IOException e){
                    Logger.debug("Nem sikerült!");
                }
            }
        }
    }

    @FXML
    private void onExitToMainMenuAlert(Alert alert) throws IOException{
        Stage stage = (Stage) alert.getOwner();
        Parent root = FXMLLoader.load(getClass().getResource("/HomeScreen.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
        Logger.debug("Kattints a kilépésre!");
    }

    @FXML
    private void onExitToMainMenu(ActionEvent event) throws IOException {
        Alert exitConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        exitConfirmation.setTitle("Vissza a főoldalra");
        exitConfirmation.setHeaderText("Biztos vissza akarsz lépni a főoldalra?");
        exitConfirmation.setContentText("Minden korábbi eredményed elvész!");

        Optional<ButtonType> result = exitConfirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/HomeScreen.fxml"));

            stage.setScene(new Scene(root));
            stage.show();

            Logger.debug("Kattints a kilépésre!");
        }
    }
}
