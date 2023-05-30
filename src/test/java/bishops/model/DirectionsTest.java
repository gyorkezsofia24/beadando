package bishops.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectionsTest {

    @Test
    void testOf() {
        assertSame(Directions.DOWN_LEFT_ONE, Directions.of(1, -1));
        assertSame(Directions.DOWN_LEFT_TWO, Directions.of(2, -2));
        assertSame(Directions.DOWN_LEFT_THREE, Directions.of(3, -3));
        assertSame(Directions.DOWN_RIGHT_ONE, Directions.of(1, 1));
        assertSame(Directions.DOWN_RIGHT_TWO, Directions.of(2, 2));
        assertSame(Directions.DOWN_RIGHT_THREE, Directions.of(3, 3));
        assertSame(Directions.UP_LEFT_ONE, Directions.of(-1, -1));
        assertSame(Directions.UP_LEFT_TWO, Directions.of(-2, -2));
        assertSame(Directions.UP_LEFT_THREE, Directions.of(-3, -3));
        assertSame(Directions.UP_RIGHT_ONE, Directions.of(-1, 1));
        assertSame(Directions.UP_RIGHT_TWO, Directions.of(-2, 2));
        assertSame(Directions.UP_RIGHT_THREE, Directions.of(-3, 3));
    }

    @Test
    void testOf_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Directions.of(0, 0));
        assertThrows(IllegalArgumentException.class, () -> Directions.of(4, 4));
        assertThrows(IllegalArgumentException.class, () -> Directions.of(1, 0));
        assertThrows(IllegalArgumentException.class, () -> Directions.of(0, 1));
    }
}
