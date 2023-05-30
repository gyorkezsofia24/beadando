package bishops.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    Position position;

    @BeforeEach
    void init() {
        position = new Position(0, 0);
    }

    @Test
    void testMoveTo() {
        assertPosition(-1, -1, Directions.UP_LEFT_ONE);
        assertPosition(-2, -2, Directions.UP_LEFT_TWO);
        assertPosition(-3, -3, Directions.UP_LEFT_THREE);
        assertPosition(-1, 1, Directions.UP_RIGHT_ONE);
        assertPosition(-2, 2, Directions.UP_RIGHT_TWO);
        assertPosition(-3, 3, Directions.UP_RIGHT_THREE);
        assertPosition(1, -1, Directions.DOWN_LEFT_ONE);
        assertPosition(2, -2, Directions.DOWN_LEFT_TWO);
        assertPosition(3, -3, Directions.DOWN_LEFT_THREE);
        assertPosition(1, 1, Directions.DOWN_RIGHT_ONE);
        assertPosition(2, 2, Directions.DOWN_RIGHT_TWO);
        assertPosition(3, 3, Directions.DOWN_RIGHT_THREE);
    }

    @Test
    void testToString() {
        assertEquals("(0,0)", position.toString());
    }

    private void assertPosition(int expectedRow, int expectedCol, Direction direction) {
        Position newPosition = position.moveTo(direction);
        assertEquals(expectedRow, newPosition.row());
        assertEquals(expectedCol, newPosition.col());
    }
}
