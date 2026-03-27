package util;

import org.junit.jupiter.api.Test;
import service.ExperimentService;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpServiceListTest {
// проверяем что метод list возвращает все эксперименты в коллекции
    @Test
    void listShouldReturnAllExperiments() {
        ExperimentService service = new ExperimentService();

        service.add("exp1", "desc1", "user");
        service.add("exp2", "desc2", "user");

        var list = service.list();

        assertEquals(2, list.size());
    }
}
