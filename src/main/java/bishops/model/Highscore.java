package bishops.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Highscore {

    private StringProperty name = new SimpleStringProperty("");

    private int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public StringProperty nameProperty(){
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }
}

