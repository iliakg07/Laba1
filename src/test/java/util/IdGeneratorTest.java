package util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IdGeneratorTest {

//  Тестим, генерирует ли метод последовательные айди

    @Test
    void shouldGenerateSequentialIds() {
        IdGenerator generator = new IdGenerator();

        long id1 = generator.generateId();
        long id2 = generator.generateId();
        long id3 = generator.generateId();

        assertEquals(1, id1);
        assertEquals(2, id2);
        assertEquals(3, id3);
    }
}
