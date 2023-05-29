package bishops.model;

import javafx.beans.property.ObjectProperty;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import org.tinylog.Logger;

import java.util.*;

public class BishopsModel{

    public static int HEIGHT = 5;

    public static int WIDTH = 4;

    private Piece[] pieces;

    public BishopsModel() {
        this(new Piece(PieceType.BLACK, new Position(0,0)),
                new Piece(PieceType.BLACK, new Position(0,1)),
                new Piece(PieceType.BLACK, new Position(0,2)),
                new Piece(PieceType.BLACK, new Position(0,3)),
                new Piece(PieceType.WHITE, new Position(4,0)),
                new Piece(PieceType.WHITE, new Position(4,1)),
                new Piece(PieceType.WHITE, new Position(4,2)),
                new Piece(PieceType.WHITE, new Position(4,3)));
    }

    public BishopsModel(Piece... pieces) {
        checkPieces(pieces);
        this.pieces = Arrays.copyOf(pieces, pieces.length);
    }

    private void checkPieces(Piece[] pieces) {
        if (pieces.length != 8) {
            throw new IllegalArgumentException();
        }
        Set<Position> seen = new HashSet<>();
        int blackPieces = 0;
        int whitePieces = 0;
        for (Piece piece : pieces) {
            Position position = piece.getPosition();
            if (!isOnBoard(position) || seen.contains(position)) {
                throw new IllegalArgumentException();
            }
            PieceType type = piece.getType();
            if (type == PieceType.BLACK) {
                blackPieces++;
            } else if (type == PieceType.WHITE) {
                whitePieces++;
            }
            seen.add(position);
        }
        if (whitePieces != 4 || blackPieces != 4) {
            throw new IllegalArgumentException();
        }
    }


    public static boolean isOnBoard(Position position){
        return 0 <= position.row() && position.row() < HEIGHT
                && 0 <= position.col() && position.col() < WIDTH;
    }

    public int getPieceCount(){
        return pieces.length;
    }

    public PieceType getPieceType(int pieceNumber) {
        return pieces[pieceNumber].getType();
    }

    public Position getPiecePosition(int pieceNumber) {
        return pieces[pieceNumber].getPosition();
    }

    public ObjectProperty<Position> positionProperty(int pieceNumber) {
        return pieces[pieceNumber].positionProperty();
    }

    public boolean isValidMove(int pieceNumber, Directions direction) {
        if (pieceNumber < 0 || pieceNumber >= pieces.length) {
            throw new IllegalArgumentException();
        }
        Position newPosition = pieces[pieceNumber].getPosition().moveTo(direction);
        if (! isOnBoard(newPosition)){
            return false;
        }
        for (var piece : pieces) {
            if (piece.getPosition().equals(newPosition)) {
                return false;
            }
        }
        for (var checkDirection : Directions.values()){
            Position checkPosition = newPosition.moveTo(checkDirection);
            for(var piece : pieces){
                if(piece.getPosition().equals(checkPosition)){
                    if (piece.getType() != pieces[pieceNumber].getType()){
                        return false;
                    }
                }
            }
        }
        for (var reverseDirection : Directions.values()){
            if (Math.signum(reverseDirection.getRowChange()) == Math.signum(direction.getRowChange()) ||
                    Math.signum(reverseDirection.getColChange()) == Math.signum(direction.getColChange())){
                continue;
            }else {
                Position reversePosition = newPosition.moveTo(reverseDirection);
                for(var piece : pieces){
                    if(piece.getPosition().equals(reversePosition)){
                        if (piece != pieces[pieceNumber]){
                            return false;
                        }else {
                            return true;
                        }
                    }
                }
            }
        }
        return true;
    }

    public Set<Directions> getValidMoves(int pieceNumber) {
        return Arrays.stream(Directions.values())
                .filter(direction -> isValidMove(pieceNumber, direction))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Directions.class)));
    }

    public void move(int pieceNumber, Directions direction){
        pieces[pieceNumber].moveTo(direction);
    }

    public List<Position> getPiecePositions() {
        return Arrays.stream(pieces)
                .map(Piece::getPosition)
                .collect(Collectors.toList());
    }

    public OptionalInt getPieceNumber(Position position) {
        return IntStream.range(0, pieces.length)
                .filter(i -> pieces[i].getPosition().equals(position))
                .findFirst();
    }


    public boolean isGoal(){
        for(var piece : pieces){
            var pieceType = piece.getType();
            switch (pieceType){
                case BLACK -> {
                    if (piece.getPosition().row() != 4){
                        return false;
                    }
                }
                case WHITE -> {
                    if (piece.getPosition().row() != 0){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void restart() {
        int blackColumns = 0;
        int whiteColumns = 0;
        for (var piece : pieces) {
            var type = piece.getType();
            int row = type == PieceType.BLACK ? 0 : 4; //kedvencem az orarol
            int col = type == PieceType.BLACK ? blackColumns++ : whiteColumns++;
            var position = new Position(row, col);
            piece.positionProperty().set(position);
        }
        Logger.debug(this);
    }

    public String toString() {
        return "[" + Arrays.stream(pieces).map(Object::toString).collect(Collectors.joining(",")) + "]";
    }
}

