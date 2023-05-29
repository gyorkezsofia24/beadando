package bishops.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Piece {

    private final PieceType type;

    private final ObjectProperty<Position> position = new SimpleObjectProperty<>();

    public Piece(PieceType type, Position position){
        this.type = type;
        this.position.set(position);
    }

    public PieceType getType() {
        return type;
    }

    public ObjectProperty<Position> positionProperty(){
        return position;
    }

    public Position getPosition() {
        return position.get();
    }

    public String toString(){
        return type.toString() + position.get().toString();
    }

    public void moveTo(Direction direction){
        Position newPosition = position.get().moveTo(direction);
        position.set(newPosition);
    }
}

