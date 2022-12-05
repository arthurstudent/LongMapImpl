import org.junit.Before;
import org.junit.Test;
import org.map.LongMap;
import org.map.LongMapImpl;

import java.util.Arrays;

import static org.junit.Assert.*;

public class LongMapImplTest {
    LongMap<String> longMap;

    @Before
    public void init() {
        longMap = new LongMapImpl<>();
        longMap.put(1L, "First");
        longMap.put(2L, "Second");
        longMap.put(3L, "Third");
        longMap.put(4L, "Fourth");
        longMap.put(5L, "Fifth");
    }

    @Test
    public void methodPutCheck() {
        assertEquals(5, longMap.size());

        String sixth = longMap.put(6L, "Sixth");

        assertEquals(6, longMap.size());
        assertNull(sixth);
    }

    @Test
    public void putWithDuplicateKeyCheck() {
        String expectedValue = "Duplicate";
        longMap.put(1L, expectedValue);

        assertEquals(expectedValue, longMap.get(1L));
    }

    @Test
    public void methodGetCheck() {
        assertEquals("Third", longMap.get(3L));
    }

    @Test
    public void getWithInvalidKeyCheck() {
        assertNull(longMap.get(10L));
    }

    @Test
    public void containsKeyOkBehaviorCheck() {
        assertTrue(longMap.containsKey(4L));
        assertTrue(longMap.containsKey(2L));
    }

    @Test
    public void containsKeyWithInvalidKeyCheck() {
        assertFalse(longMap.containsKey(11L));
    }

    @Test
    public void containsValueOkBehaviorCheck() {
        assertTrue(longMap.containsValue("First"));
        assertTrue(longMap.containsValue("Second"));
    }

    @Test
    public void containsValueWithInvalidKeyCheck() {
        assertFalse(longMap.containsValue("Ninth"));
    }

    @Test
    public void keysMethodCheck() {
        longMap.put(11L, "11");
        longMap.put(31L, "31");
        longMap.put(22L, "22");
        longMap.put(40L, "40");

        long[] expectedKeys = new long[]{1, 2, 11, 3, 22, 31, 4, 5, 40};
        long[] keys = longMap.keys();

        Arrays.sort(expectedKeys);
        Arrays.sort(keys);

        assertEquals(expectedKeys.length, keys.length);
        assertArrayEquals(expectedKeys, keys);
    }

    @Test
    public void valuesMethodCheck() {
        String[] expectedValues = new String[]{"First", "Second", "Third", "Fourth", "Fifth"};
        String[] values = longMap.values();

        assertEquals(expectedValues.length, values.length);
        assertArrayEquals(expectedValues, values);
    }

    @Test
    public void isEmptyMethodCheck() {
        assertFalse(longMap.isEmpty());
    }

    @Test(timeout = 10000)
    public void highLoadWithRandomKeysCheck() {
        LongMap<Double> longMap = new LongMapImpl<>();

        for (long i = 0; i < 10000; i++) {
            longMap.put(i, i / 10.5);
        }
        assertEquals(10000, longMap.size());
    }

    @Test
    public void constructorWithNegativeValueCheck() {
        int negativeValue = -1;
        assertThrows(IllegalArgumentException.class, () -> new LongMapImpl<>(negativeValue));
        assertThrows(IllegalArgumentException.class, () -> new LongMapImpl<>(10, negativeValue));
    }

    @Test
    public void removeCheck() {
        String expected = "First";
        String removedValue = longMap.remove(1L);

        assertEquals(expected, removedValue);

        longMap.remove(2L);
        longMap.remove(3L);
        longMap.remove(4L);
        longMap.remove(5L);

        assertEquals(0, longMap.size());
        assertTrue(longMap.isEmpty());
    }

    @Test
    public void removeWithInvalidKeyCheck() {
        assertNull(longMap.remove(21L));
    }

    @Test
    public void clearMethodCheck() {
        longMap.clear();

        assertTrue(longMap.isEmpty());
        assertEquals(0, longMap.size());
        assertNull(longMap.get(1L));
    }
}
